package org.cuelang.cue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ValueTest {
    CueContext ctx;

    @BeforeEach
    void setUp() {
        this.ctx = new CueContext();
    }

    @Test
    void toValueFromLong() {
        new Value(ctx, -1);
    }

    @Test
    void toValueAsUnsigned() {
        new Value(ctx, 0xcafebabeL);
    }

    @Test
    void toValueFromBoolean() {
        new Value(ctx, true);
    }

    @Test
    void toValueFromDouble() {
        new Value(ctx, 0.123);
    }

    @Test
    void toValueFromString() {
        new Value(ctx, "hello");
    }

    @Test
    void toValueFromBytes() {
        byte[] buf = {1, 2, 3, 4, 5};
        new Value(ctx, buf);
    }

    @Test
    void kind() {
        assertDoesNotThrow(() -> {
            Value v;

            v = ctx.compile("null");
            assertEquals(CueKind.NULL, v.kind());

            v = ctx.compile("true");
            assertEquals(CueKind.BOOL, v.kind());

            v = ctx.compile("42");
            assertEquals(CueKind.INT, v.kind());

            v = ctx.compile("123.456");
            assertEquals(CueKind.FLOAT, v.kind());

            v = ctx.compile("\"hello\"");
            assertEquals(CueKind.STRING, v.kind());

            v = ctx.compile("'\\x03abc'");
            assertEquals(CueKind.BYTES, v.kind());

            v = ctx.compile("{ a: 42 }");
            assertEquals(CueKind.STRUCT, v.kind());

            v = ctx.compile("[1, 2.0, \"hello\"]");
            assertEquals(CueKind.LIST, v.kind());
        });
    }

    @Test
    void incompleteKind() {
        assertDoesNotThrow(() -> {
            Value v;

            v = ctx.bottom();
            assertEquals(CueKind.BOTTOM, v.incompleteKind());

            v = ctx.compile("bool");
            assertEquals(CueKind.BOOL, v.incompleteKind());

            v = ctx.compile("int");
            assertEquals(CueKind.INT, v.incompleteKind());

            v = ctx.compile("float");
            assertEquals(CueKind.FLOAT, v.incompleteKind());

            v = ctx.compile("string");
            assertEquals(CueKind.STRING, v.incompleteKind());

            v = ctx.compile("bytes");
            assertEquals(CueKind.BYTES, v.incompleteKind());

            v = ctx.compile("{ a: int }");
            assertEquals(CueKind.STRUCT, v.incompleteKind());

            v = ctx.compile("[int, float, string]");
            assertEquals(CueKind.LIST, v.incompleteKind());

            v = ctx.compile("number");
            assertEquals(CueKind.NUMBER, v.incompleteKind());

            v = ctx.top();
            assertEquals(CueKind.TOP, v.incompleteKind());
        });
    }
}