package fastkeyboard.benchmark;

import fastkeyboard.FastKeyboard;
import fastkeyboard.FastKeyboardImpl;
import fastkeyboard.KeyboardDevice;
import org.openjdk.jmh.annotations.*;

import java.util.List;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Benchmark)
@Warmup(iterations = 2, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 3, time = 1, timeUnit = TimeUnit.SECONDS)
@Fork(1)
public class Benchmark {

    private FastKeyboard keyboard;

    @Setup
    public void setup() {
        try {
            keyboard = new FastKeyboardImpl();
        } catch (Exception e) {
            keyboard = null;
        }
    }

    @org.openjdk.jmh.annotations.Benchmark
    public List<KeyboardDevice> benchmarkGetConnectedDevices() {
        if (keyboard != null) {
            return keyboard.getConnectedDevices();
        }
        return null;
    }
}
