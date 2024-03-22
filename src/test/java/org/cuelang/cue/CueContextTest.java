package org.cuelang.cue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CueContextTest {
    CueContext ctx;

    @BeforeEach
    void setUp() {
        this.ctx = new CueContext();
    }

    @Test
    void top() {
        ctx.top();
    }

    @Test
    void topIsTOP() {
        var v = ctx.top();
        assertEquals(CueKind.TOP, v.incompleteKind());
    }

    @Test
    void bottom() {
        ctx.bottom();
    }

    @Test
    void bottomIsBOTTOM() {
        var v = ctx.bottom();
        assertEquals(CueKind.BOTTOM, v.incompleteKind());
    }

    @Test
    void compileEmpty() {
        assertDoesNotThrow(() -> {
            ctx.compile("");
        });
    }

    @Test
    void compileEmptyWithOptions() {
        assertDoesNotThrow(() -> {
            ctx.compile("", new BuildOption.FileName("empty.cue"), new BuildOption.ImportPath("example.com/foo/bar"));
        });
    }

    @Test
    void compileScalarWithOptions() {
        assertDoesNotThrow(() -> {
           ctx.compile("int", new BuildOption.FileName("empty.cue"), new BuildOption.ImportPath("example.com/foo/bar"));
        });
    }

    @Test
    void compileEmptyBytes() {
        byte[] buf = {};
        assertDoesNotThrow(() -> {
            ctx.compile(buf);
        });
    }

    @Test
    void compileEmptyBytesWithOptions() {
        byte[] buf = {};
        assertDoesNotThrow(() -> {
            ctx.compile(buf, new BuildOption.FileName("empty.cue"), new BuildOption.ImportPath("example.com/foo/bar"));
        });
    }

    @Test
    void compileBytesWithOptions() {
        assertDoesNotThrow(() -> {
           ctx.compile("int".getBytes(), new BuildOption.FileName("empty.cue"), new BuildOption.ImportPath("example.com/foo/bar"));
        });
    }

    @Test
    void compileBottom() {
        assertThrows(CueError.class, () -> ctx.compile("_|_"));
    }

    @Test
    void toValueFromLong() {
        assertDoesNotThrow(() ->
                assertEquals(-1, ctx.toValue(-1).getLong())
        );
    }

    @Test
    void toValueAsUnsigned() {
        assertDoesNotThrow(() ->
                assertEquals(0xcafebabeL, ctx.toValueAsUnsigned(0xcafebabeL).getLongAsUnsigned())
        );
    }

    @Test
    void toValueFromBoolean() {
        assertDoesNotThrow(() ->
                assertTrue(ctx.toValue(true).getBoolean())
        );
    }

    @Test
    void toValueFromDouble() {
        assertDoesNotThrow(() ->
                assertEquals(0.123, ctx.toValue(0.123).getDouble())
        );
    }

    @Test
    void toValueFromString() {
        ctx.toValue("hello");
    }

    @Test
    void toValueFromBytes() {
        byte[] buf = {1, 2, 3, 4, 5};
        ctx.toValue(buf);
    }
}