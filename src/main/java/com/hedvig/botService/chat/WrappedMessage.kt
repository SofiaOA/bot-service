package com.hedvig.botService.chat

import com.hedvig.botService.enteties.UserContext
import com.hedvig.botService.enteties.message.Message
import com.hedvig.botService.enteties.message.MessageBody

data class WrappedMessage<T: MessageBody>(val message:T, val callback:(T, UserContext, Message) -> String)
