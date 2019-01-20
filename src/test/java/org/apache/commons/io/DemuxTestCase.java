/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.io;

import org.apache.commons.io.input.DemuxInputStream;
import java.lang.String;
import org.apache.commons.io.output.DemuxOutputStream;
import java.lang.Object;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Random;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.commons.io.testtools.TestUtils;
import org.junit.Test;

/**
 * Basic unit tests for the multiplexing streams.
 */
public class DemuxTestCase {
    private static final String T1 = "Thread1";
    private static final String T2 = "Thread2";
    private static final String T3 = "Thread3";
    private static final String T4 = "Thread4";

    private static final String DATA1 = "Data for thread1";
    private static final String DATA2 = "Data for thread2";
    private static final String DATA3 = "Data for thread3";
    private static final String DATA4 = "Data for thread4";

    private static final Random c_random = new Random();
    private final HashMap<String, ByteArrayOutputStream> m_outputMap = new HashMap<String, ByteArrayOutputStream>();
    private final HashMap<String, Thread> m_threadMap = new HashMap<String, Thread>();

    @SuppressWarnings("deprecation") // unavoidable until Java 7
    private String getOutput(final String threadName) {
        final ByteArrayOutputStream output =
                m_outputMap.get(threadName);
        assertNotNull("getOutput()", output);

        return output.toString(Charsets.UTF_8);
    }

    private String getInput(final String threadName) {
        final ReaderThread thread = (ReaderThread) m_threadMap.get(threadName);
        assertNotNull("getInput()", thread);

        return thread.getData();
    }

    private void doStart()
            throws Exception {
        for (String name : m_threadMap.keySet()) {
            final Thread thread = m_threadMap.get(name);
            thread.start();
        }
    }

    private void doJoin()
            throws Exception {
        for (String name : m_threadMap.keySet()) {
            final Thread thread = m_threadMap.get(name);
            thread.join();
        }
    }

    private void startWriter(final String name,
                             final String data,
                             final DemuxOutputStream demux)
            throws Exception {
        final ByteArrayOutputStream output = new ByteArrayOutputStream();
        m_outputMap.put(name, output);
        final WriterThread thread =
                new WriterThread(name, data, output, demux);
        m_threadMap.put(name, thread);
    }

    private void startReader(final String name,
                             final String data,
                             final DemuxInputStream demux)
            throws Exception {
        final ByteArrayInputStream input = new ByteArrayInputStream(data.getBytes());
        final ReaderThread thread = new ReaderThread(name, input, demux);
        m_threadMap.put(name, thread);
    }

    @Test
	public void testOutputStream() throws Exception {
		this.demuxTestCaseTestStreamTemplate(new DemuxTestCaseTestOutputStreamAdapterImpl(), DemuxOutputStream.class);
	}

    @Test
	public void testInputStream() throws Exception {
		this.demuxTestCaseTestStreamTemplate(new DemuxTestCaseTestInputStreamAdapterImpl(), DemuxInputStream.class);
	}

    private static class ReaderThread
            extends Thread {
        private final StringBuffer m_buffer = new StringBuffer();
        private final InputStream m_input;
        private final DemuxInputStream m_demux;

        ReaderThread(final String name,
                     final InputStream input,
                     final DemuxInputStream demux) {
            super(name);
            m_input = input;
            m_demux = demux;
        }

        public String getData() {
            return m_buffer.toString();
        }

        @Override
        public void run() {
            m_demux.bindStream(m_input);

            try {
                int ch = m_demux.read();
                while (-1 != ch) {
                    //System.out.println( "Reading: " + (char)ch );
                    m_buffer.append((char) ch);

                    final int sleepTime = Math.abs(c_random.nextInt() % 10);
                    TestUtils.sleep(sleepTime);
                    ch = m_demux.read();
                }
            } catch (final Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static class WriterThread
            extends Thread {
        private final byte[] m_data;
        private final OutputStream m_output;
        private final DemuxOutputStream m_demux;

        WriterThread(final String name,
                     final String data,
                     final OutputStream output,
                     final DemuxOutputStream demux) {
            super(name);
            m_output = output;
            m_demux = demux;
            m_data = data.getBytes();
        }

        @Override
        public void run() {
            m_demux.bindStream(m_output);
            for (final byte element : m_data) {
                try {
                    //System.out.println( "Writing: " + (char)m_data[ i ] );
                    m_demux.write(element);
                    final int sleepTime = Math.abs(c_random.nextInt() % 10);
                    TestUtils.sleep(sleepTime);
                } catch (final Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

	public <TDemuxStream extends Object> void demuxTestCaseTestStreamTemplate(
			DemuxTestCaseTestStreamAdapter<TDemuxStream> adapter, Class<TDemuxStream> clazzTDemuxStream)
			throws Exception {
		final TDemuxStream v1 = clazzTDemuxStream.newInstance();
		adapter.start(T1, DATA1, v1);
		adapter.start(T2, DATA2, v1);
		adapter.start(T3, DATA3, v1);
		adapter.start(T4, DATA4, v1);
		doStart();
		doJoin();
		assertEquals("Data1", DATA1, adapter.get(T1));
		assertEquals("Data2", DATA2, adapter.get(T2));
		assertEquals("Data3", DATA3, adapter.get(T3));
		assertEquals("Data4", DATA4, adapter.get(T4));
	}

	interface DemuxTestCaseTestStreamAdapter<TDemuxStream> {
		void start(String string1, String string2, TDemuxStream tDemuxStream1) throws Exception;

		String get(String string1);
	}

	class DemuxTestCaseTestOutputStreamAdapterImpl implements DemuxTestCaseTestStreamAdapter<DemuxOutputStream> {
		public void start(String T1, String DATA1, DemuxOutputStream output) throws Exception {
			startWriter(T1, DATA1, output);
		}

		public String get(String T1) {
			return getOutput(T1);
		}
	}

	class DemuxTestCaseTestInputStreamAdapterImpl implements DemuxTestCaseTestStreamAdapter<DemuxInputStream> {
		public void start(String T1, String DATA1, DemuxInputStream input) throws Exception {
			startReader(T1, DATA1, input);
		}

		public String get(String T1) {
			return getInput(T1);
		}
	}
}

