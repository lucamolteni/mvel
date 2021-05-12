package org.mvel2;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import jdk.jshell.JShell;
import jdk.jshell.Snippet;
import jdk.jshell.Snippet.Status;
import jdk.jshell.SnippetEvent;
import jdk.jshell.VarSnippet;

import static java.lang.String.format;

class ExampleJShell {

    private JShell js;
    private Map vars;

    public ExampleJShell(Map vars) {
        this.vars = vars;

        JShell.Builder builder = JShell.builder();

        this.js = builder.build();
    }

    public Object eval(String input)  {

        List<SnippetEvent> events = js.eval(input);
        for (SnippetEvent e : events) {
            StringBuilder sb = new StringBuilder();
            if (e.causeSnippet() == null) {
                //  We have a snippet creation event
                switch (e.status()) {
                    case VALID:
                        sb.append("Successful ");
                        break;
                    case RECOVERABLE_DEFINED:
                        sb.append("With unresolved references ");
                        break;
                    case RECOVERABLE_NOT_DEFINED:
                        sb.append("Possibly reparable, failed  ");
                        break;
                    case REJECTED:
                        sb.append("Failed ");
                        break;
                }
                if (e.previousStatus() == Status.NONEXISTENT) {
                    sb.append("addition");
                } else {
                    sb.append("modification");
                }
                sb.append(" of ");
                Snippet snippet = e.snippet();
                sb.append(snippet.source());
                log(sb.toString());
                String value = e.value();
                if (value != null) {
                    log(String.format("Value is: %s%n", e.value()));
                }
                if(value == null) {
                    return sb.toString();
                } else {
                    if(snippet instanceof VarSnippet) {
                        VarSnippet varSnippet = (VarSnippet) snippet;

                        String typeName = varSnippet.typeName();

                        // this type value could be used

                        return value;
                    }
                    snippet.kind();
                    return value;
                }
            }
        }
        return input;
    }

    Logger logger  = Logger.getLogger(ExampleJShell.class.getName());

    private void log(String sb) {
//        logger.info(sb);
    }

    public void initEnvironmentVariable() {
        for(Object kv : vars.entrySet()) {
            Map.Entry<String, Object> entry = (Map.Entry<String, Object>) kv;
            if(entry.getValue() != null) {
                String assignVar = assignVar(entry);
                eval(assignVar);
            }
        }
    }

    private String assignVar(Map.Entry<String, Object> entry) {
        String key = entry.getKey();
        Object value = entry.getValue();
        if(value instanceof Date) {
            Date d = (Date) value;
            return format("java.util.Date %s = new java.util.Date(%sl);", key, d.getTime());
        } else if(value instanceof String) {
            return format("String %s = \"%s\";", key, value);
        } else if(value instanceof Integer) {
            return format("int %s = %s;", key, value);
        } else {
            return format("var %s = \"%s\";", key, value.toString());
        }
    }
}
