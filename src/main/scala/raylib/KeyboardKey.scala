package raylib

opaque type KeyboardKey = Int

object KeyboardKey:
  private def apply(value: Int): KeyboardKey = value
  
  val NULL            = apply(0)        // Key: NULL used for no key pressed
  // Alphanumeric keys
  val APOSTROPHE      = apply(39)       // Key: '
  val COMMA           = apply(44)       // Key: ,
  val MINUS           = apply(45)       // Key: -
  val PERIOD          = apply(46)       // Key: .
  val SLASH           = apply(47)       // Key: /
  val ZERO            = apply(48)       // Key: 0
  val ONE             = apply(49)       // Key: 1
  val TWO             = apply(50)       // Key: 2
  val THREE           = apply(51)       // Key: 3
  val FOUR            = apply(52)       // Key: 4
  val FIVE            = apply(53)       // Key: 5
  val SIX             = apply(54)       // Key: 6
  val SEVEN           = apply(55)       // Key: 7
  val EIGHT           = apply(56)       // Key: 8
  val NINE            = apply(57)       // Key: 9
  val SEMICOLON       = apply(59)       // Key: ;
  val EQUAL           = apply(61)       // Key: =
  val A               = apply(65)       // Key: A | a
  val B               = apply(66)       // Key: B | b
  val C               = apply(67)       // Key: C | c
  val D               = apply(68)       // Key: D | d
  val E               = apply(69)       // Key: E | e
  val F               = apply(70)       // Key: F | f
  val G               = apply(71)       // Key: G | g
  val H               = apply(72)       // Key: H | h
  val I               = apply(73)       // Key: I | i
  val J               = apply(74)       // Key: J | j
  val K               = apply(75)       // Key: K | k
  val L               = apply(76)       // Key: L | l
  val M               = apply(77)       // Key: M | m
  val N               = apply(78)       // Key: N | n
  val O               = apply(79)       // Key: O | o
  val P               = apply(80)       // Key: P | p
  val Q               = apply(81)       // Key: Q | q
  val R               = apply(82)       // Key: R | r
  val S               = apply(83)       // Key: S | s
  val T               = apply(84)       // Key: T | t
  val U               = apply(85)       // Key: U | u
  val V               = apply(86)       // Key: V | v
  val W               = apply(87)       // Key: W | w
  val X               = apply(88)       // Key: X | x
  val Y               = apply(89)       // Key: Y | y
  val Z               = apply(90)       // Key: Z | z
  val LEFT_BRACKET    = apply(91)       // Key: [
  val BACKSLASH       = apply(92)       // Key: '\'
  val RIGHT_BRACKET   = apply(93)       // Key: ]
  val GRAVE           = apply(96)       // Key: `
  // Function keys
  val SPACE           = apply(32)       // Key: Space
  val ESCAPE          = apply(256)      // Key: Esc
  val ENTER           = apply(257)      // Key: Enter
  val TAB             = apply(258)      // Key: Tab
  val BACKSPACE       = apply(259)      // Key: Backspace
  val INSERT          = apply(260)    // Key: Ins
  val DELETE          = apply(261)    // Key: Del
  val RIGHT           = apply(262)    // Key: Cursor right
  val LEFT            = apply(263)    // Key: Cursor left
  val DOWN            = apply(264)    // Key: Cursor down
  val UP              = apply(265)    // Key: Cursor up
  val PAGE_UP         = apply(266)    // Key: Page up
  val PAGE_DOWN       = apply(267)    // Key: Page down
  val HOME            = apply(268)    // Key: Home
  val END             = apply(269)    // Key: End
  val CAPS_LOCK       = apply(280)    // Key: Caps lock
  val SCROLL_LOCK     = apply(281)    // Key: Scroll down
  val NUM_LOCK        = apply(282)    // Key: Num lock
  val PRINT_SCREEN    = apply(283)    // Key: Print screen
  val PAUSE           = apply(284)    // Key: Pause
  val F1              = apply(290)    // Key: F1
  val F2              = apply(291)    // Key: F2
  val F3              = apply(292)    // Key: F3
  val F4              = apply(293)    // Key: F4
  val F5              = apply(294)    // Key: F5
  val F6              = apply(295)    // Key: F6
  val F7              = apply(296)    // Key: F7
  val F8              = apply(297)    // Key: F8
  val F9              = apply(298)    // Key: F9
  val F10             = apply(299)    // Key: F10
  val F11             = apply(300)    // Key: F11
  val F12             = apply(301)    // Key: F12
  val LEFT_SHIFT      = apply(340)    // Key: Shift left
  val LEFT_CONTROL    = apply(341)    // Key: Control left
  val LEFT_ALT        = apply(342)    // Key: Alt left
  val LEFT_SUPER      = apply(343)    // Key: Super left
  val RIGHT_SHIFT     = apply(344)    // Key: Shift right
  val RIGHT_CONTROL   = apply(345)    // Key: Control right
  val RIGHT_ALT       = apply(346)    // Key: Alt right
  val RIGHT_SUPER     = apply(347)    // Key: Super right
  val KB_MENU         = apply(348)    // Key: KB menu
  // Keypad keys
  val KP_0            = apply(320)    // Key: Keypad 0
  val KP_1            = apply(321)    // Key: Keypad 1
  val KP_2            = apply(322)    // Key: Keypad 2
  val KP_3            = apply(323)    // Key: Keypad 3
  val KP_4            = apply(324)    // Key: Keypad 4
  val KP_5            = apply(325)    // Key: Keypad 5
  val KP_6            = apply(326)    // Key: Keypad 6
  val KP_7            = apply(327)    // Key: Keypad 7
  val KP_8            = apply(328)    // Key: Keypad 8
  val KP_9            = apply(329)    // Key: Keypad 9
  val KP_DECIMAL      = apply(330)    // Key: Keypad .
  val KP_DIVIDE       = apply(331)    // Key: Keypad /
  val KP_MULTIPLY     = apply(332)    // Key: Keypad *
  val KP_SUBTRACT     = apply(333)    // Key: Keypad -
  val KP_ADD          = apply(334)    // Key: Keypad +
  val KP_ENTER        = apply(335)    // Key: Keypad Enter
  val KP_EQUAL        = apply(336)    // Key: Keypad =
  // Android key buttons
  val BACK            = apply(4)       // Key: Android back button
  val MENU            = apply(5)       // Key: Android menu button
  val VOLUME_UP       = apply(24)      // Key: Android volume up button
  val VOLUME_DOWN     = apply(25)      // Key: Android volume down button

