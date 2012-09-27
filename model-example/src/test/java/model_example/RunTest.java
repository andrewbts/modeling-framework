/**
 * Copyright (c) 2012 The Regents of the University of California.
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. Neither the name of the University nor the names of its contributors
 *    may be used to endorse or promote products derived from this software
 *    without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE REGENTS AND CONTRIBUTORS ``AS IS'' AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED.  IN NO EVENT SHALL THE REGENTS OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS
 * OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
 * LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY
 * OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 */

package model_example;

import org.junit.*;
import static org.junit.Assert.*;

import java.lang.Math;

import edu.berkeley.path.model_elements.*;
import edu.berkeley.path.model_mock.MockLogSink;
import edu.berkeley.path.model_base.LogEntry;

public class RunTest {
  public Context ctx;
  public Run run;
  public State state;
  public DensitySink densnk;
  
  @Before
  public void setup() {
    ctx = new Context(1);
    run = ctx.makeRun(1, 1234, 5678);
    state = run.makeState();
    run.initializeState(state);
    densnk = (DensitySink)(run.getSink("density"));
  }
  
  @Test
  public void testDensityOutput() {
    assertEquals(null, densnk.fetch(0));
    assertEquals(null, densnk.fetch(1));
    assertEquals(null, densnk.fetch(2));
    
    run.runForSteps(1);
    assertTrue(null != densnk.fetch(0)); // junit y u no have assertNotEquals?
    assertEquals(null, densnk.fetch(1));
    assertEquals(null, densnk.fetch(2));

    run.runForSteps(1);
    assertTrue(null != densnk.fetch(0));
    assertTrue(null != densnk.fetch(1));
    assertEquals(null, densnk.fetch(2));
    
    DensityProfile dp = (DensityProfile)densnk.fetch(1);
    
    //System.out.println("\n\ndp = " + dp + "\n\n");
    // output is (in JSON generated by avro):
    //  dp = {"id": "2out", "vehiclesPerMeter": {"3": [21.370252752202436, 21.924625321996107, 22.897159091992364]}}
    
    assertEquals((Double)21.3702, dp.getVehiclesPerMeter().get("3").get(0), 0.0001);
  }

  @Test
  public void testLogSink() {
    MockLogSink logsnk = (MockLogSink)(run.getSink("log"));

    run.runForSteps(1);
    run.runForSteps(1);
    
    LogEntry entry = logsnk.getEntry(0);
    assertEquals("Initialized model_example.State", entry.getMessage());
    
    entry = logsnk.getEntry(1);
    assertEquals("Updated model_example.State", entry.getMessage());
  }
}
