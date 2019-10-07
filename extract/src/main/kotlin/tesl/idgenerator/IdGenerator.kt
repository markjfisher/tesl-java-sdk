package tesl.idgenerator

import com.fasterxml.uuid.Generators
import com.fasterxml.uuid.impl.NameBasedGenerator
import java.util.UUID

object IdGenerator {
    private val teslCardsNamespaceUUID = Generators.nameBasedGenerator(NameBasedGenerator.NAMESPACE_OID).generate("The namespace of TESL Cards")
    private fun generateUUID(namespace: UUID, id: String) = Generators.nameBasedGenerator(namespace).generate(id).toString()

    fun generateCardUUID(id: String) = generateUUID(teslCardsNamespaceUUID, id)
}
