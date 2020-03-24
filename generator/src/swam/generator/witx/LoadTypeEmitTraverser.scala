package swam.generator.witx

import swam.witx.traverser.TypesTraverser
import swam.witx.unresolved.{
  AliasType,
  ArrayType,
  BaseWitxType,
  BasicType,
  EnumType,
  FlagsType,
  Handle,
  Pointer,
  StructType,
  UnionType
}

/**
  * @author Javier Cabrera-Arteaga on 2020-03-23
  */
class LoadTypeEmitTraverser(prev: String, types: Map[String, BaseWitxType], offset: String = "", mem: String = "")
    extends TypesTraverser[String](types) {

  override val basicTypeTraverser = {
    case (_, t: BasicType) =>
      t.name match {
        case "u8"     => s"$mem.get($offset $prev)\n"
        case "u16"    => s"$mem.getShort($offset $prev)\n"
        case "u32"    => s"$mem.getInt($offset $prev)\n"
        case "u64"    => s"$mem.getLong($offset  $prev)\n"
        case "s64"    => s"$mem.getLong($offset $prev)\n"
        case "string" => s"$mem.getInt($offset $prev)\n"
      }
  }

  def getVal(tpe: BaseWitxType): String = tpe match {
    case x: AliasType  => x.tpeName
    case x: BasicType  => x.tpeName
    case x: EnumType   => s"${x.tpeName}Enum.Value"
    case x: FlagsType  => s"${x.tpeName}Flags.Value"
    case x: Pointer    => s"Pointer[${getVal(x.tpe)}]"
    case x: Handle     => x.tpeName
    case x: StructType => x.tpeName
    case x: UnionType  => x.tpeName
    case x: ArrayType  => x.tpeName
  }

  override val aliasTypeTraverser = {
    case (_, t: AliasType) => traverse("", types(t.tpe.tpeName))
  }

  override val enumTypeTraverser = {
    case (_, t: EnumType) => traverse("", t.tpe)
  }

  override val flagsTypeTraverser = {
    case (_, t: FlagsType) => traverse("", t.tpe)
  }

  override val structTypeTraverser = {

    case (_, t: StructType) => s"${t.tpeName}($mem, $offset $prev)"

  }

  override val handleTypeTraverser = {
    case (_, t: Handle) => s"$mem.getInt($offset $prev)\n"
  }

  override val unionTypeTraverser = {
    case (_, t: UnionType) => s"${t.tpeName}($mem, $offset $prev)"

  }

  override val arrayTypeTraverser = {
    case (_, t: ArrayType) => s"$mem.getInt($offset $prev)\n"
  }

  override val pointerTypeTraverser = {
    case (_, p: Pointer) =>
      s"new Pointer[${getVal(p.tpe)}]($offset, (i) => ${new LoadTypeEmitTraverser("", types, offset, "mem")
        .traverse("", p.tpe)}, (i, r) => ${new WriteTypeEmitTraverser("r", "", types, offset, mem)
        .traverse("", p.tpe)})"
  }
}