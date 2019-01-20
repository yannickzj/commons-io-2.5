package org.apache.commons.io.input;
import java.lang.Object;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;
import static org.junit.Assert.assertEquals;
import java.io.IOException;
public class NullTestTestMarkNotSupportedTemplate {
  public static <TObject extends Object,TTestNull extends TObject>void nullTestTestMarkNotSupportedTemplate(  NullTestTestMarkNotSupportedAdapter<TObject> adapter,  Class<TTestNull> clazzTTestNull) throws Exception {
    final TObject v1=clazzTTestNull.getDeclaredConstructor(int.class,boolean.class,boolean.class).newInstance(100,false,true);
    assertFalse("Mark Should NOT be Supported",adapter.markSupported(v1));
    try {
      adapter.mark(v1,5);
      fail("mark() should throw UnsupportedOperationException");
    }
 catch (    final UnsupportedOperationException e) {
      assertEquals("mark() error message","Mark not supported",e.getMessage());
    }
    try {
      adapter.reset(v1);
      fail("reset() should throw UnsupportedOperationException");
    }
 catch (    final UnsupportedOperationException e) {
      assertEquals("reset() error message","Mark not supported",e.getMessage());
    }
    adapter.close(v1);
  }
}

interface NullTestTestMarkNotSupportedAdapter<TObject> {
	boolean markSupported(TObject tObject1);

	void mark(TObject tObject1, int i1) throws IOException;

	void reset(TObject tObject1) throws IOException;

	void close(TObject tObject1) throws IOException;
}
