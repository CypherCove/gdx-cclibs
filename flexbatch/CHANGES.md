# CoveTools change log

## 1.1.1
 * Add `getRenderContext()` so additional parameters can be changed manually.
 * Allow a flush of the previous content of the batch with `repeatPreviousFlush()`. This saves a lot of CPU when redrawing content that is known to be unchanged since the last flush.
 * Add capacity getter methods.

## 1.1.0
 * Bugfix FlexBatch applying render context changes to the GL state outside of begin/end.
 * Bugfix FlexBatch was incorrectly assuming all custom Batchables would trigger a flush on the first draw.