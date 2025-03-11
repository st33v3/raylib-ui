package ui

class EncodingContext

class DecodingContext

trait SceneCodec[T]:
  def encode(value: T, ctx: EncodingContext): (EncodingContext, String)
  def decode(value: String, ctx: DecodingContext): T

object SceneCodec:
  /**
   * A typeclass instance for a volatile value - value that will not be serialized
   */
  def transient[T]: SceneCodec[T] = new SceneCodec[T]:
    def encode(value: T, ctx: EncodingContext): (EncodingContext, String) = (ctx, "")
    def decode(value: String, ctx: DecodingContext): T = throw new UnsupportedOperationException("Volatile value cannot be decoded")

  given SceneCodec[Widget] = transient
  given SceneCodec[WidgetFactory[?]] = transient
  given SceneCodec[Unit] = transient
  given [A: SceneCodec] => SceneCodec[Map[String, A]] = transient