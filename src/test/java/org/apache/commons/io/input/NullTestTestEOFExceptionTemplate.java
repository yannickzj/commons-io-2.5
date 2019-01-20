package org.apache.commons.io.input;
import java.lang.Object;
import static org.junit.Assert.assertEquals;
import java.io.IOException;
import static org.junit.Assert.fail;
import java.io.EOFException;
import java.lang.String;
public class NullTestTestEOFExceptionTemplate {
  public static <TObject extends Object,TTestNull extends TObject>void nullTestTestEOFExceptionTemplate(  NullTestTestEOFExceptionAdapter<TObject> adapter,  Class<TTestNull> clazzTTestNull,  String string1) throws Exception {
    final TObject v1=clazzTTestNull.getDeclaredConstructor(int.class,boolean.class,boolean.class).newInstance(2,false,true);
    assertEquals("Read 1",0,adapter.read(v1));
    assertEquals("Read 2",1,adapter.read(v1));
    try {
      final int result=adapter.read(v1);
      fail(string1 + result + "]");
    }
 catch (    final EOFException e) {
    }
    adapter.close(v1);
  }
}

interface NullTestTestEOFExceptionAdapter<TObject> {
	int read(TObject tObject1) throws IOException;

	void close(TObject tObject1) throws IOException;
}
