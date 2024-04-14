package ee.schimke.emulatortools.perfetto

import com.squareup.wire.Message
import com.squareup.wire.ProtoAdapter
import io.ktor.http.ContentType
import io.ktor.http.content.ByteArrayContent
import io.ktor.http.content.OutgoingContent
import io.ktor.serialization.ContentConverter
import io.ktor.util.reflect.TypeInfo
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.charsets.Charset
import io.ktor.utils.io.jvm.javaio.toInputStream
import perfetto.protos.AppendTraceDataResult
import perfetto.protos.QueryResult

class ProtoConverter : ContentConverter {
    override suspend fun deserialize(charset: Charset, typeInfo: TypeInfo, content: ByteReadChannel): Any? {
        return runCatching {
            val adapter = protoAdapter(typeInfo)
            adapter.decode(content.toInputStream())
        }.getOrNull()
    }

    private fun protoAdapter(typeInfo: TypeInfo): ProtoAdapter<*> = when (typeInfo.type.qualifiedName) {
        QueryResult::class.qualifiedName -> QueryResult.ADAPTER
        AppendTraceDataResult::class.qualifiedName -> AppendTraceDataResult.ADAPTER
        else -> TODO()
    }

    override suspend fun serializeNullable(
        contentType: ContentType,
        charset: Charset,
        typeInfo: TypeInfo,
        value: Any?
    ): OutgoingContent? {
        return runCatching {
            val adapter = (value as Message<*, *>).adapter as ProtoAdapter<Any>
            val bytes = adapter.encode(value!!)
            ByteArrayContent(bytes)
        }.getOrNull()
    }
}