package ee.schimke.emulatortools.perfetto

import org.junit.jupiter.api.Test
import kotlin.test.Ignore

@Ignore("Needs external trace file")
class RunPerfettoQueryMain {
  @Test
  fun testMain() {
    main(
      "-t",
      "/Users/yschimke/Downloads/sample.perfetto-trace",
      "-q",
      "select ts, t.name, value from counter as c left join counter_track t on c.track_id = t.id where t.name = 'batt.current_ua'"
    )
  }

  @Test
  fun testMainWithProcessor() {
    main(
      "-p",
      "src/jvmMain/resources/trace_processor",
      "-t",
      "/Users/yschimke/Downloads/sample.perfetto-trace",
      "-q",
      "select ts, t.name, value from counter as c left join counter_track t on c.track_id = t.id where t.name = 'batt.current_ua'"
    )
  }
}