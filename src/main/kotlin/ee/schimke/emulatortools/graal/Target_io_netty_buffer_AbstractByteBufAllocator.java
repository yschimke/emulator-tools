package ee.schimke.emulatortools.graal;

import com.oracle.svm.core.annotate.Substitute;
import com.oracle.svm.core.annotate.TargetClass;
import io.grpc.netty.shaded.io.netty.buffer.AbstractByteBufAllocator;
import io.grpc.netty.shaded.io.netty.buffer.ByteBuf;

@TargetClass(AbstractByteBufAllocator.class)
final class TargetAbstractByteBufAllocator {
    @Substitute
    protected static ByteBuf toLeakAwareBuffer(ByteBuf buf) {
        return buf;
    }
}