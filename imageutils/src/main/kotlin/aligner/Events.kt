package aligner

import tornadofx.EventBus.RunOn.BackgroundThread
import tornadofx.FXEvent

object NextCardRequest : FXEvent(BackgroundThread)
object MoveUpRequest : FXEvent(BackgroundThread)
object MoveDownRequest : FXEvent(BackgroundThread)
object MoveLeftRequest : FXEvent(BackgroundThread)
object MoveRightRequest : FXEvent(BackgroundThread)
object ScaleIncreaseRequest : FXEvent(BackgroundThread)
object ScaleDecreaseRequest : FXEvent(BackgroundThread)
