package tesl.bot

import com.jessecorbett.diskord.api.rest.Embed
import com.jessecorbett.diskord.api.rest.FileData

data class ReplyData(
    val text: List<String>,
    val embed: Embed? = null,
    val fileData: FileData? = null
)