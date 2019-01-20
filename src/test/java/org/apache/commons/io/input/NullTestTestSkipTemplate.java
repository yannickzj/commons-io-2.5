package org.apache.commons.io.input;
import java.lang.Object;
import static org.junit.Assert.assertEquals;
import java.io.IOException;
import static org.junit.Assert.fail;
public class NullTestTestSkipTemplate {
  public static <TObject extends Object,TTestNull extends TObject>void nullTestTestSkipTemplate(  NullTestTestSkipAdapter<TObject> adapter,  Class<TTestNull> clazzTTestNull) throws Exception {
    final TObject v1=clazzTTestNull.getDeclaredConstructor(int.class,boolean.class,boolean.class).newInstance(10,true,false);
    assertEquals("Read 1",0,adapter.read(v1));
    assertEquals("Read 2",1,adapter.read(v1));
    assertEquals("Skip 1",5,adapter.skip(v1,5));
    assertEquals("Read 3",7,adapter.read(v1));
    assertEquals("Skip 2",2,adapter.skip(v1,5));
    assertEquals("Skip 3 (EOF)",-1,adapter.skip(v1,5));
    try {
      adapter.skip(v1,5);
      fail("Expected IOException for skipping after end of file");
    }
 catch (    final IOException e) {
      assertEquals("Skip after EOF IOException message","Skip after end of file",e.getMessage());
    }
    adapter.close(v1);
  }
}

interface NullTestTestSkipAdapter<TObject> {
	int read(TObject tObject1) throws IOException;

	long skip(TObject tObject1, long l1) throws IOException;

	void close(TObject tObject1) throws IOException;
}
