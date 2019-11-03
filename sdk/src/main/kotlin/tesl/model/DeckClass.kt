package tesl.model

import tesl.model.ClassColour.*
import java.awt.Color

enum class ClassColour(val hexColor: Color) {
    GREEN(Color(0x1f, 0x58, 0x1f)),
    RED(Color(0xc0, 0x20, 0x20)),
    BLUE(Color(0x26, 0x9d, 0xff)),
    YELLOW(Color(0xb4, 0xb4, 0x35)),
    PURPLE(Color(0x80, 0x00, 0x80)),
    GREY(Color(0xa0, 0xa0, 0xa0))
}

enum class ClassAbility(val classColour: ClassColour) {
    AGILITY(GREEN),
    STRENGTH(RED),
    INTELLIGENCE(BLUE),
    WILLPOWER(YELLOW),
    ENDURANCE(PURPLE),
    NEUTRAL(GREY);

    companion object {
        fun from(classColour: ClassColour): ClassAbility {
            return values().find { it.classColour == classColour } ?: NEUTRAL
        }
    }
}

enum class DeckClass(val classAbilities: List<ClassAbility>) {
    NEUTRAL(listOf(ClassAbility.NEUTRAL)),

    AGILITY(listOf(ClassAbility.AGILITY)),
    STRENGTH(listOf(ClassAbility.STRENGTH)),
    INTELLIGENCE(listOf(ClassAbility.INTELLIGENCE)),
    WILLPOWER(listOf(ClassAbility.WILLPOWER)),
    ENDURANCE(listOf(ClassAbility.ENDURANCE)),

    ARCHER(listOf(ClassAbility.AGILITY, ClassAbility.STRENGTH)),
    ASSASSIN(listOf(ClassAbility.AGILITY, ClassAbility.INTELLIGENCE)),
    BATTLEMAGE(listOf(ClassAbility.INTELLIGENCE, ClassAbility.STRENGTH)),
    CRUSADER(listOf(ClassAbility.STRENGTH, ClassAbility.WILLPOWER)),
    MAGE(listOf(ClassAbility.INTELLIGENCE, ClassAbility.WILLPOWER)),
    MONK(listOf(ClassAbility.AGILITY, ClassAbility.WILLPOWER)),
    SCOUT(listOf(ClassAbility.AGILITY, ClassAbility.ENDURANCE)),
    SORCERER(listOf(ClassAbility.ENDURANCE, ClassAbility.INTELLIGENCE)),
    SPELLSWORD(listOf(ClassAbility.ENDURANCE, ClassAbility.WILLPOWER)),
    WARRIOR(listOf(ClassAbility.ENDURANCE, ClassAbility.STRENGTH)),

    HOUSE_DAGOTH(listOf(ClassAbility.AGILITY, ClassAbility.INTELLIGENCE, ClassAbility.STRENGTH)),
    HOUSE_HLAALU(listOf(ClassAbility.STRENGTH, ClassAbility.WILLPOWER, ClassAbility.AGILITY)),
    HOUSE_REDORAN(listOf(ClassAbility.STRENGTH, ClassAbility.WILLPOWER, ClassAbility.ENDURANCE)),
    HOUSE_TELVANNI(listOf(ClassAbility.INTELLIGENCE, ClassAbility.AGILITY, ClassAbility.ENDURANCE)),
    TRIBUNAL_TEMPLE(listOf(ClassAbility.INTELLIGENCE, ClassAbility.WILLPOWER, ClassAbility.ENDURANCE)),

    ALDMERI_DOMINION(listOf(ClassAbility.INTELLIGENCE, ClassAbility.WILLPOWER, ClassAbility.AGILITY)),
    DAGGERFALL_COVENANT(listOf(ClassAbility.ENDURANCE, ClassAbility.STRENGTH, ClassAbility.INTELLIGENCE)),
    EBONHEART_PACT(listOf(ClassAbility.AGILITY, ClassAbility.ENDURANCE, ClassAbility.STRENGTH)),
    EMPIRE_OF_CYRODIIL(listOf(ClassAbility.WILLPOWER, ClassAbility.AGILITY, ClassAbility.ENDURANCE)),
    GUILDSWORN(listOf(ClassAbility.STRENGTH, ClassAbility.INTELLIGENCE, ClassAbility.WILLPOWER))
}
