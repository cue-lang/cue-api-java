package org.cuelang.cue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AttributeTest {
    CueContext ctx;

    @BeforeEach
    void setUp() {
        this.ctx = new CueContext();
    }

    @Test
    void argCount() {
        assertDoesNotThrow(() -> {
            Value v;
            Attribute[] attrs;

            v = ctx.compile("@foo()");
            attrs = v.attributes();
            assertEquals(1, attrs[0].argCount());

            v = ctx.compile("@foo(bar)");
            attrs = v.attributes();
            assertEquals(1, attrs[0].argCount());

            v = ctx.compile("@foo(bar, baz)");
            attrs = v.attributes();
            assertEquals(2, attrs[0].argCount());

            v = ctx.compile("@foo(bar, baz=qux, qux=quux, 1)");
            attrs = v.attributes();
            assertEquals(4, attrs[0].argCount());
        });
    }

    @Test
    void name() {
        assertDoesNotThrow(() -> {
            Value v;
            Attribute[] attrs;

            v = ctx.compile("""
                    @foo()
                    @bar(baz)
                    
                    x: int @qux(quux)
                    """);
            attrs = v.attributes();

            assertEquals("foo", attrs[0].name());
            assertEquals("bar", attrs[1].name());

            var x = v.lookup("x");
            var a = x.attributes()[0];
            assertEquals("qux", a.name());
        });
    }

    @Test
    void value() {
        assertDoesNotThrow(() -> {
            Value v;
            Attribute[] attrs;

            v = ctx.compile("""
                    @foo()
                    @bar(baz)
                    
                    x: int @qux(quux)
                    """);
            attrs = v.attributes();

            assertEquals("", attrs[0].value());
            assertEquals("baz", attrs[1].value());

            var x = v.lookup("x");
            var a = x.attributes()[0];
            assertEquals("quux", a.value());
        });
    }

    @Test
    void arg() {
        assertDoesNotThrow(() -> {
            Value v;
            Attribute[] attrs;

            v = ctx.compile("""
                    @foo()
                    @bar(foo, bar, baz=qux)
                    
                    x: int @baz(1, foo, bar=baz)
                    """);
            attrs = v.attributes();

            assertEquals(new Attribute.Arg.Value(""), attrs[0].arg(0));
            assertEquals(new Attribute.Arg.Value("foo"), attrs[1].arg(0));
            assertEquals(new Attribute.Arg.Value("bar"), attrs[1].arg(1));
            assertEquals(new Attribute.Arg.KeyValue("baz", "qux"), attrs[1].arg(2));

            var x = v.lookup("x");
            var a = x.attributes()[0];
            assertEquals(new Attribute.Arg.Value("1"), a.arg(0));
            assertEquals(new Attribute.Arg.Value("foo"), a.arg(1));
            assertEquals(new Attribute.Arg.KeyValue("bar", "baz"), a.arg(2));
        });
    }

    @Test
    void argError() {
        assertThrows(IndexOutOfBoundsException.class, () -> {
            var v = ctx.compile("@foo()");
            v.attributes()[0].arg(1);
        });

        assertThrows(IndexOutOfBoundsException.class, () -> {
            var v = ctx.compile("@foo(0, 1, foo=2)");
            v.attributes()[0].arg(3);
        });
    }

    @Test
    void args() {
        assertDoesNotThrow(() -> {
            Value v;
            Attribute[] attrs;

            v = ctx.compile("""
                    @foo()
                    @bar(foo, bar, baz=qux)
                    
                    x: int @baz(1, foo, bar=baz)
                    """);
            attrs = v.attributes();

            assertArrayEquals(new Attribute.Arg[] {
                    new Attribute.Arg.Value(""),
            }, attrs[0].args());

            assertArrayEquals(new Attribute.Arg[] {
                    new Attribute.Arg.Value("foo"),
                    new Attribute.Arg.Value("bar"),
                    new Attribute.Arg.KeyValue("baz", "qux"),
            }, attrs[1].args());

            var x = v.lookup("x");
            var a = x.attributes()[0];
            assertArrayEquals(new Attribute.Arg[] {
                    new Attribute.Arg.Value("1"),
                    new Attribute.Arg.Value("foo"),
                    new Attribute.Arg.KeyValue("bar", "baz"),
            }, a.args());
        });
    }
}