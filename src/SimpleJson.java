import java.util.*;

/** Minimal JSON parser & writer (objects/arrays/strings/numbers/true/false/null). */
public class SimpleJson {
    // ---------- Public API ----------
    public static Object parse(String s) {
        return new Parser(s).parse();
    }

    public static String stringify(Object obj, boolean pretty) {
        StringBuilder sb = new StringBuilder();
        new Writer(pretty).write(obj, sb, 0);
        return sb.toString();
    }

    // ---------- Parser ----------
    private static class Parser {
        private final String s;
        private int i = 0;
        Parser(String s) { this.s = s; }

        Object parse() {
            skipWs();
            Object v = readValue();
            skipWs();
            if (i != s.length()) throw err("Trailing data");
            return v;
        }

        private Object readValue() {
            skipWs();
            if (i >= s.length()) throw err("Unexpected end");
            char c = s.charAt(i);
            if (c == '{') return readObject();
            if (c == '[') return readArray();
            if (c == '"') return readString();
            if (c == '-' || Character.isDigit(c)) return readNumber();
            if (s.startsWith("true", i)) { i += 4; return Boolean.TRUE; }
            if (s.startsWith("false", i)) { i += 5; return Boolean.FALSE; }
            if (s.startsWith("null", i)) { i += 4; return null; }
            throw err("Bad value");
        }

        private Map<String, Object> readObject() {
            LinkedHashMap<String, Object> obj = new LinkedHashMap<>();
            expect('{'); skipWs();
            if (peek('}')) { i++; return obj; }
            while (true) {
                skipWs();
                String key = readString();
                skipWs(); expect(':'); skipWs();
                Object val = readValue();
                obj.put(key, val);
                skipWs();
                if (peek('}')) { i++; break; }
                expect(',');
            }
            return obj;
        }

        private List<Object> readArray() {
            ArrayList<Object> arr = new ArrayList<>();
            expect('['); skipWs();
            if (peek(']')) { i++; return arr; }
            while (true) {
                Object v = readValue();
                arr.add(v);
                skipWs();
                if (peek(']')) { i++; break; }
                expect(',');
            }
            return arr;
        }

        private String readString() {
            expect('"');
            StringBuilder sb = new StringBuilder();
            while (i < s.length()) {
                char c = s.charAt(i++);
                if (c == '"') break;
                if (c == '\\') {
                    if (i >= s.length()) throw err("Bad escape");
                    char e = s.charAt(i++);
                    switch (e) {
                        case '"': sb.append('"'); break;
                        case '\\': sb.append('\\'); break;
                        case '/': sb.append('/'); break;
                        case 'b': sb.append('\b'); break;
                        case 'f': sb.append('\f'); break;
                        case 'n': sb.append('\n'); break;
                        case 'r': sb.append('\r'); break;
                        case 't': sb.append('\t'); break;
                        case 'u':
                            if (i+4 > s.length()) throw err("Bad \\u escape");
                            int cp = Integer.parseInt(s.substring(i, i+4), 16);
                            sb.append((char) cp); i += 4; break;
                        default: throw err("Bad escape");
                    }
                } else {
                    sb.append(c);
                }
            }
            return sb.toString();
        }

        private Number readNumber() {
            int start = i;
            if (s.charAt(i) == '-') i++;
            while (i < s.length() && Character.isDigit(s.charAt(i))) i++;
            if (i < s.length() && s.charAt(i) == '.') {
                i++;
                while (i < s.length() && Character.isDigit(s.charAt(i))) i++;
                return Double.parseDouble(s.substring(start, i));
            } else {
                return Long.parseLong(s.substring(start, i));
            }
        }

        private void skipWs() {
            while (i < s.length()) {
                char c = s.charAt(i);
                if (c == ' ' || c == '\n' || c == '\r' || c == '\t') i++;
                else break;
            }
        }

        private void expect(char c) {
            if (i >= s.length() || s.charAt(i) != c) throw err("Expected '" + c + "'");
            i++;
        }

        private boolean peek(char c) {
            return (i < s.length() && s.charAt(i) == c);
        }

        private RuntimeException err(String msg) {
            return new RuntimeException(msg + " at pos " + i);
        }
    }

    // ---------- Writer ----------
    private static class Writer {
        private final boolean pretty;
        Writer(boolean pretty) { this.pretty = pretty; }

        void write(Object v, StringBuilder sb, int depth) {
            if (v == null) sb.append("null");
            else if (v instanceof String) writeString((String) v, sb);
            else if (v instanceof Number || v instanceof Boolean) sb.append(v.toString());
            else if (v instanceof Map) writeObject((Map<?, ?>) v, sb, depth);
            else if (v instanceof List) writeArray((List<?>) v, sb, depth);
            else throw new RuntimeException("Unsupported: " + v.getClass());
        }

        private void writeObject(Map<?, ?> m, StringBuilder sb, int depth) {
            sb.append("{");
            if (!m.isEmpty()) {
                int i = 0;
                for (Map.Entry<?, ?> e : m.entrySet()) {
                    if (i++ > 0) sb.append(",");
                    newline(sb, depth+1);
                    writeString(String.valueOf(e.getKey()), sb);
                    sb.append(": ");
                    write(e.getValue(), sb, depth+1);
                }
                newline(sb, depth);
            }
            sb.append("}");
        }

        private void writeArray(List<?> arr, StringBuilder sb, int depth) {
            sb.append("[");
            if (!arr.isEmpty()) {
                for (int i = 0; i < arr.size(); i++) {
                    if (i > 0) sb.append(",");
                    newline(sb, depth+1);
                    write(arr.get(i), sb, depth+1);
                }
                newline(sb, depth);
            }
            sb.append("]");
        }

        private void writeString(String s, StringBuilder sb) {
            sb.append('"');
            for (int i = 0; i < s.length(); i++) {
                char c = s.charAt(i);
                switch (c) {
                    case '"': sb.append("\\\""); break;
                    case '\\': sb.append("\\\\"); break;
                    case '\b': sb.append("\\b"); break;
                    case '\f': sb.append("\\f"); break;
                    case '\n': sb.append("\\n"); break;
                    case '\r': sb.append("\\r"); break;
                    case '\t': sb.append("\\t"); break;
                    default:
                        if (c < 0x20) {
                            sb.append(String.format("\\u%04x", (int)c));
                        } else sb.append(c);
                }
            }
            sb.append('"');
        }

        private void newline(StringBuilder sb, int depth) {
            if (!pretty) return;
            sb.append("\n");
            for (int i = 0; i < depth; i++) sb.append("  ");
        }
    }
}