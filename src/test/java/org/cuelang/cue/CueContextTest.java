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
    void bottom() {
        ctx.bottom();
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
            ctx.compile("", new Build.FileName("empty.cue"), new Build.ImportPath("example.com/foo/bar"));
        });
    }

    @Test
    void compileScalarWithOptions() {
        assertDoesNotThrow(() -> {
           ctx.compile("int", new Build.FileName("empty.cue"), new Build.ImportPath("example.com/foo/bar"));
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
            ctx.compile(buf, new Build.FileName("empty.cue"), new Build.ImportPath("example.com/foo/bar"));
        });
    }

    @Test
    void compileBytesWithOptions() {
        assertDoesNotThrow(() -> {
           ctx.compile("int".getBytes(), new Build.FileName("empty.cue"), new Build.ImportPath("example.com/foo/bar"));
        });
    }

    @Test
    void compileBottom() {
        assertThrows(CueError.class, () -> ctx.compile("_|_"));
    }

    @Test
    void toValueFromLong() {
        ctx.toValue(-1);
    }

    @Test
    void toValueAsUnsigned() {
        ctx.toValueAsUnsigned(0xcafebabeL);
    }

    @Test
    void toValueFromBoolean() {
        ctx.toValue(true);
    }

    @Test
    void toValueFromDouble() {
        ctx.toValue(0.123);
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