package org.mvel2.tests.core;

import java.util.Map;

public class CompoundAssignmentOperatorTestEval {

    public Object eval(Map map) {
        int simpleInteger = (int) map.get("simpleInteger");

        {
            simpleInteger += 10;
        }

        map.put("simpleInteger", simpleInteger);

        return simpleInteger;
    }
}
