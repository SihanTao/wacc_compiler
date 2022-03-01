package instruction.addrMode2

import register.ARM11Register

class RegOffset(val Rn: ARM11Register, val sign: Sign ,val Rm: ARM11Register): AddrMode2 {
}
