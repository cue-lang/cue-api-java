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

    @Test
    void equals() {
        assertDoesNotThrow(() -> {
            assertTrue(ctx.compile("null").equals(ctx.compile("null")));

            assertTrue(ctx.compile("true").equals(ctx.compile("true")));
            assertTrue(ctx.compile("false").equals(ctx.compile("false")));

            assertFalse(ctx.compile("true").equals(ctx.compile("false")));
            assertFalse(ctx.compile("false").equals(ctx.compile("true")));

            assertTrue(ctx.compile("1").equals(ctx.compile("1")));
            assertTrue(ctx.compile("-2").equals(ctx.compile("-2")));
            assertTrue(
                    ctx.compile("123456789123456781234567").
                            equals(ctx.compile("123456789123456781234567"))
            );

            assertFalse(ctx.compile("1").equals(ctx.compile("0")));
            assertFalse(ctx.compile("-2").equals(ctx.compile("-1")));
            assertFalse(
                    ctx.compile("123456789123456781234567").
                            equals(ctx.compile("123456789123456781234568"))
            );

            assertTrue(ctx.compile("1.1").equals(ctx.compile("1.1")));
            assertTrue(ctx.compile("-2.1").equals(ctx.compile("-2.1")));
            assertTrue(
                    ctx.compile("123456789123456781234567.11223344").
                            equals(ctx.compile("123456789123456781234567.11223344"))
            );

            assertFalse(ctx.compile("1.1").equals(ctx.compile("0")));
            assertFalse(ctx.compile("-2.1").equals(ctx.compile("-1.1")));
            assertFalse(
                    ctx.compile("123456789123456781234567.11223344").
                            equals(ctx.compile("123456789123456781234567.11223345"))
            );

            assertTrue(ctx.compile("\"\"").equals(ctx.compile("\"\"")));
            assertTrue(ctx.compile("\"hello\"").equals(ctx.compile("\"hello\"")));

            assertFalse(ctx.compile("\"\"").equals(ctx.compile("\"x\"")));
            assertFalse(ctx.compile("\"hello\"").equals(ctx.compile("\"goodbye\"")));

            assertTrue(ctx.compile("'\\x03abc'").equals(ctx.compile("'\\x03abc'")));
            assertFalse(ctx.compile("'\\x03abc'").equals(ctx.compile("'\\x01abc'")));

            assertTrue(ctx.compile("{ a: 1 }").equals(ctx.compile("{ a: 1 }")));
            assertTrue(
                    ctx.compile("{ a: 1, b: \"hello\" }").
                            equals(ctx.compile("{ a: 1, b: \"hello\" }"))
            );
            assertTrue(
                    ctx.compile("{ a: 1, b: \"hello\", c: d: e: true }").
                            equals(ctx.compile("{ a: 1, b: \"hello\", c: d: e: true }"))
            );

            assertFalse(ctx.compile("{ a: 1 }").equals(ctx.compile("{ a: 2 }")));
            assertFalse(ctx.compile("{ a: 1 }").equals(ctx.compile("{ a: 1, b: 2 }")));
            assertFalse(ctx.compile("{ a: 1 }").equals(ctx.compile("{ b: 1 }")));
            assertFalse(
                    ctx.compile("{ a: 1, b: \"hello\" }").
                            equals(ctx.compile("{ a: 1, b: \"goodbye\" }"))
            );
            assertFalse(
                    ctx.compile("{ a: 1, b: \"hello\", c: d: e: true }").
                            equals(ctx.compile("{ a: 1, b: \"hello\", c: d: e: false }"))
            );
            assertFalse(
                    ctx.compile("{ a: 1, b: \"hello\", c: d: e: true }").
                            equals(ctx.compile("{ a: 1, b: \"hello\", c: d: e: true, x: 42 }"))
            );

            assertTrue(ctx.compile("[]").equals(ctx.compile("[]")));
            assertTrue(ctx.compile("[1, \"hi\", true]").equals(ctx.compile("[1, \"hi\", true]")));

            assertFalse(ctx.compile("[]").equals(ctx.compile("{}")));
            assertFalse(ctx.compile("[1, \"hi\", true]").equals(ctx.compile("[1, \"goodbye\", true]")));
        });
    }

    @Test
    void lookup() {
        assertDoesNotThrow(() -> {
            var v = ctx.compile("{ a: 1, b: { c: true, d: \"hello\" } }");

            var a = v.lookup("a");
            assertTrue(a.equals(ctx.compile("1")));

            var b = v.lookup("b");

            var c = b.lookup("c");
            assertTrue(c.equals(ctx.compile("true")));

            var d = b.lookup("d");
            assertTrue(d.equals(ctx.compile("\"hello\"")));
        });
    }

    @Test
    void lookupInvalid() {
        assertDoesNotThrow(() -> {
            var v = ctx.compile("a: b: c: d: 1");

            assertThrows(CueError.class, () -> v.lookup("x"));

            var a = v.lookup("a");
            assertThrows(CueError.class, () -> a.lookup("x"));
        });
    }

    @Test
    void getLong() {
        assertDoesNotThrow(() -> {
            Value v;

            v = ctx.compile("1");
            assertEquals(1, v.getLong());

            v = ctx.compile("-123");
            assertEquals(-123, v.getLong());
        });
    }

    @Test
    void getLongError() {
        assertDoesNotThrow(() -> {
            var v0 = ctx.compile("int");
            assertThrows(CueError.class, v0::getLong);
            assertThrows(CueError.class, v0::getLong);

            var v1 = ctx.compile("123.456");
            assertThrows(CueError.class, v1::getLong);

            var v2 = ctx.compile("1234567890123456789012345678901234567890");
            assertThrows(CueError.class, v2::getLong);
        });
    }

    @Test
    void getLongAsUnsigned() {
        assertDoesNotThrow(() -> {
            Value v;

            v = ctx.compile("1");
            assertEquals(1, v.getLongAsUnsigned());

            v = ctx.compile("18_446_744_073_709_551_615");
            assertEquals(-1, v.getLongAsUnsigned());
        });
    }

    @Test
    void getBoolean() {
        assertDoesNotThrow(() -> {
            Value v;

            v = ctx.compile("true");
            assertTrue(v.getBoolean());

            v = ctx.compile("false");
            assertFalse(v.getBoolean());
        });
    }

    @Test
    void getBooleanError() {
        assertDoesNotThrow(() -> {
            var v0 = ctx.compile("bool");
            assertThrows(CueError.class, v0::getBoolean);

            var v1 = ctx.compile("123.456");
            assertThrows(CueError.class, v1::getBoolean);
        });
    }

    @Test
    void getDouble() {
        assertDoesNotThrow(() -> {
            Value v;

            v = ctx.compile("123.456");
            assertEquals(123.456, v.getDouble());

            v = ctx.compile("12345678901234.567");
            assertEquals(12345678901234.567, v.getDouble());
        });
    }

    @Test
    void getDoubleError() {
        assertDoesNotThrow(() -> {
            var v0 = ctx.compile("float");
            assertThrows(CueError.class, v0::getDouble);

            var v1 = ctx.compile("true");
            assertThrows(CueError.class, v1::getDouble);

            var v2 = ctx.compile("1.797693134862315708145274237317043567981e+308 + 1");
            assertThrows(CueError.class, v2::getDouble);
        });
    }

    @Test
    void getString() {
        assertDoesNotThrow(() -> {
            var v = ctx.toValue("hello");
            assertEquals("hello", v.getString());
        });
    }

    @Test
    void getStringError() {
        assertDoesNotThrow(() -> {
            var v0 = ctx.compile("float");
            assertThrows(CueError.class, v0::getString);

            var v1 = ctx.compile("true");
            assertThrows(CueError.class, v1::getString);

            var v2 = ctx.compile("{ a: \"hello\" }");
            assertThrows(CueError.class, v2::getString);
        });
    }

    @Test
    void getBytes() {
        assertDoesNotThrow(() -> {
            var v = ctx.compile("'\\xde\\xad\\xbe\\xef'");
            assertArrayEquals(
                    new byte[] {(byte) 0xde, (byte) 0xad, (byte) 0xbe, (byte) 0xef},
                    v.getBytes()
            );
        });
    }

    @Test
    void getBytesError() {
        assertDoesNotThrow(() -> {
            var v0 = ctx.compile("float");
            assertThrows(CueError.class, v0::getBytes);

            var v1 = ctx.compile("true");
            assertThrows(CueError.class, v1::getBytes);

            var v2 = ctx.compile("{ a: \"hello\" }");
            assertThrows(CueError.class, v2::getBytes);
        });
    }

    @Test
    void getJSON() {
        assertDoesNotThrow(() -> {
            var v = ctx.compile("a: b: c: 42");
            assertEquals("{\"a\":{\"b\":{\"c\":42}}}", v.getJSON());
        });
    }

    @Test
    void getJSONError() {
        assertDoesNotThrow(() -> {
            var v0 = ctx.compile("float");
            assertThrows(CueError.class, v0::getJSON);

            var v2 = ctx.compile("{ a: int }");
            assertThrows(CueError.class, v2::getJSON);
        });
    }

    @Test
    void unifyTopWithScalar() {
        assertDoesNotThrow(() -> {
            assertTrue(ctx.toValue(true).unify(ctx.top()).getBoolean());
            assertEquals(1, ctx.toValue(1).unify(ctx.top()).getLong());
            assertEquals(123.4, ctx.toValue(123.4).unify(ctx.top()).getDouble());
        });
    }

    @Test
    void unifyStruct() {
        assertDoesNotThrow(() -> {
            var foo = ctx.compile("""
                    a: int
                    b: _
                    """);
            var bar = ctx.compile("""
                    a: 42
                    b: string
                    c: true
                    """);

            var fooBar = foo.unify(bar);

            assertEquals(42, fooBar.lookup("a").getLong());
            assertEquals(CueKind.STRING, fooBar.lookup("b").incompleteKind());
            assertTrue(fooBar.lookup("c").getBoolean());
        });
    }

    @Test
    void unifyError() {
        assertDoesNotThrow(() -> {
            var foo = ctx.compile("x: 1");
            var bar = ctx.compile("x: 2");

            var fooBar = foo.unify(bar);
            var err = fooBar.Error();
            assertTrue(err.isPresent());
        });
    }

    @Test
    void unifyStructAssociativity() {
        assertDoesNotThrow(() -> {
            var foo = ctx.compile("""
                    a: b: c: int
                    p: number
                    x: y: _
                    """);
            var bar = ctx.compile("""
                    a: b: c: 42
                    """);
            var baz = ctx.compile("""
                    p: -1
                    x: y: "hello"
                    """);

            var fooBarBaz = foo.unify(bar).unify(baz);
            var fooBazBar = foo.unify(baz).unify(bar);
            var barFooBaz = bar.unify(foo).unify(baz);
            var barBazFoo = bar.unify(baz).unify(foo);
            var bazFooBar = baz.unify(foo).unify(bar);
            var bazBarFoo = baz.unify(bar).unify(foo);

            assertTrue(fooBarBaz.equals(fooBazBar));
            assertTrue(fooBarBaz.equals(barFooBaz));
            assertTrue(fooBarBaz.equals(barBazFoo));
            assertTrue(fooBarBaz.equals(bazFooBar));
            assertTrue(fooBarBaz.equals(bazBarFoo));

            assertTrue(fooBazBar.equals(barFooBaz));
            assertTrue(fooBazBar.equals(barBazFoo));
            assertTrue(fooBazBar.equals(bazFooBar));
            assertTrue(fooBazBar.equals(bazBarFoo));

            assertTrue(barFooBaz.equals(barBazFoo));
            assertTrue(barFooBaz.equals(bazFooBar));
            assertTrue(barFooBaz.equals(bazBarFoo));

            assertTrue(barBazFoo.equals(bazFooBar));
            assertTrue(barBazFoo.equals(bazBarFoo));
        });
    }
}