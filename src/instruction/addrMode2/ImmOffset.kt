package instruction.addrMode2

import register.ARM11Register

class ImmOffset(val Rn: ARM11Register, val offset: Int): AddrMode2 {
}