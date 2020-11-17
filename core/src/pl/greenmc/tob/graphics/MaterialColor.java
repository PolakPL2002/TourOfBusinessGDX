package pl.greenmc.tob.graphics;

import com.badlogic.gdx.graphics.Color;

@SuppressWarnings("unused")
public interface MaterialColor {
    MaterialColor AMBER = new Amber();
    MaterialColor BLUE = new Blue();
    MaterialColor CYAN = new Cyan();
    MaterialColor DEEP_ORANGE = new DeepOrange();
    MaterialColor DEEP_PURPLE = new DeepPurple();
    MaterialColor GREEN = new Green();
    MaterialColor INDIGO = new Indigo();
    MaterialColor LIGHT_BLUE = new LightBlue();
    MaterialColor LIGHT_GREEN = new LightGreen();
    MaterialColor LIME = new Lime();
    MaterialColor ORANGE = new Orange();
    MaterialColor PINK = new Pink();
    MaterialColor PURPLE = new Purple();
    MaterialColor RED = new Red();
    MaterialColor TEAL = new Teal();
    MaterialColor YELLOW = new Yellow();

    Color color50();

    Color color100();

    Color color200();

    Color color300();

    Color color400();

    Color color500();

    Color color600();

    Color color700();

    Color color800();

    Color color900();

    Color colorA100();

    Color colorA200();

    Color colorA400();

    Color colorA700();

    class Amber implements MaterialColor {
        @Override
        public Color color50() {
            return new Color(255, 248, 225, 1f);
        }

        @Override
        public Color color100() {
            return new Color(255, 236, 179, 1f);
        }

        @Override
        public Color color200() {
            return new Color(255, 224, 130, 1f);
        }

        @Override
        public Color color300() {
            return new Color(255, 213, 79, 1f);
        }

        @Override
        public Color color400() {
            return new Color(255, 202, 40, 1f);
        }

        @Override
        public Color color500() {
            return new Color(255, 193, 7, 1f);
        }

        @Override
        public Color color600() {
            return new Color(255, 179, 0, 1f);
        }

        @Override
        public Color color700() {
            return new Color(255, 160, 0, 1f);
        }

        @Override
        public Color color800() {
            return new Color(255, 143, 0, 1f);
        }

        @Override
        public Color color900() {
            return new Color(255, 111, 0, 1f);
        }

        @Override
        public Color colorA100() {
            return new Color(255, 229, 127, 1f);
        }

        @Override
        public Color colorA200() {
            return new Color(255, 215, 64, 1f);
        }

        @Override
        public Color colorA400() {
            return new Color(255, 196, 0, 1f);
        }

        @Override
        public Color colorA700() {
            return new Color(255, 171, 0, 1f);
        }

    }

    class Blue implements MaterialColor {
        @Override
        public Color color50() {
            return new Color(227, 242, 253, 1f);
        }

        @Override
        public Color color100() {
            return new Color(187, 222, 251, 1f);
        }

        @Override
        public Color color200() {
            return new Color(144, 202, 249, 1f);
        }

        @Override
        public Color color300() {
            return new Color(100, 181, 246, 1f);
        }

        @Override
        public Color color400() {
            return new Color(66, 165, 245, 1f);
        }

        @Override
        public Color color500() {
            return new Color(33, 150, 243, 1f);
        }

        @Override
        public Color color600() {
            return new Color(30, 136, 229, 1f);
        }

        @Override
        public Color color700() {
            return new Color(25, 118, 210, 1f);
        }

        @Override
        public Color color800() {
            return new Color(21, 101, 192, 1f);
        }

        @Override
        public Color color900() {
            return new Color(13, 71, 161, 1f);
        }

        @Override
        public Color colorA100() {
            return new Color(130, 177, 255, 1f);
        }

        @Override
        public Color colorA200() {
            return new Color(68, 138, 255, 1f);
        }

        @Override
        public Color colorA400() {
            return new Color(41, 121, 255, 1f);
        }

        @Override
        public Color colorA700() {
            return new Color(41, 98, 255, 1f);
        }

    }

    class Cyan implements MaterialColor {
        @Override
        public Color color50() {
            return new Color(224, 247, 250, 1f);
        }

        @Override
        public Color color100() {
            return new Color(178, 235, 242, 1f);
        }

        @Override
        public Color color200() {
            return new Color(128, 222, 234, 1f);
        }

        @Override
        public Color color300() {
            return new Color(77, 208, 225, 1f);
        }

        @Override
        public Color color400() {
            return new Color(38, 198, 218, 1f);
        }

        @Override
        public Color color500() {
            return new Color(0, 188, 212, 1f);
        }

        @Override
        public Color color600() {
            return new Color(0, 172, 193, 1f);
        }

        @Override
        public Color color700() {
            return new Color(0, 151, 167, 1f);
        }

        @Override
        public Color color800() {
            return new Color(0, 131, 143, 1f);
        }

        @Override
        public Color color900() {
            return new Color(0, 96, 100, 1f);
        }

        @Override
        public Color colorA100() {
            return new Color(132, 255, 255, 1f);
        }

        @Override
        public Color colorA200() {
            return new Color(24, 255, 255, 1f);
        }

        @Override
        public Color colorA400() {
            return new Color(0, 229, 255, 1f);
        }

        @Override
        public Color colorA700() {
            return new Color(0, 184, 212, 1f);
        }

    }

    class DeepOrange implements MaterialColor {
        @Override
        public Color color50() {
            return new Color(251, 233, 231, 1f);
        }

        @Override
        public Color color100() {
            return new Color(255, 204, 188, 1f);
        }

        @Override
        public Color color200() {
            return new Color(255, 171, 145, 1f);
        }

        @Override
        public Color color300() {
            return new Color(255, 138, 101, 1f);
        }

        @Override
        public Color color400() {
            return new Color(255, 112, 67, 1f);
        }

        @Override
        public Color color500() {
            return new Color(255, 87, 34, 1f);
        }

        @Override
        public Color color600() {
            return new Color(244, 81, 30, 1f);
        }

        @Override
        public Color color700() {
            return new Color(230, 74, 25, 1f);
        }

        @Override
        public Color color800() {
            return new Color(216, 67, 21, 1f);
        }

        @Override
        public Color color900() {
            return new Color(191, 54, 12, 1f);
        }

        @Override
        public Color colorA100() {
            return new Color(255, 158, 128, 1f);
        }

        @Override
        public Color colorA200() {
            return new Color(255, 110, 64, 1f);
        }

        @Override
        public Color colorA400() {
            return new Color(255, 61, 0, 1f);
        }

        @Override
        public Color colorA700() {
            return new Color(221, 44, 0, 1f);
        }

    }

    class DeepPurple implements MaterialColor {
        @Override
        public Color color50() {
            return new Color(237, 231, 246, 1f);
        }

        @Override
        public Color color100() {
            return new Color(209, 196, 233, 1f);
        }

        @Override
        public Color color200() {
            return new Color(179, 157, 219, 1f);
        }

        @Override
        public Color color300() {
            return new Color(149, 117, 205, 1f);
        }

        @Override
        public Color color400() {
            return new Color(126, 87, 194, 1f);
        }

        @Override
        public Color color500() {
            return new Color(103, 58, 183, 1f);
        }

        @Override
        public Color color600() {
            return new Color(94, 53, 177, 1f);
        }

        @Override
        public Color color700() {
            return new Color(81, 45, 168, 1f);
        }

        @Override
        public Color color800() {
            return new Color(69, 39, 160, 1f);
        }

        @Override
        public Color color900() {
            return new Color(49, 27, 146, 1f);
        }

        @Override
        public Color colorA100() {
            return new Color(179, 136, 255, 1f);
        }

        @Override
        public Color colorA200() {
            return new Color(124, 77, 255, 1f);
        }

        @Override
        public Color colorA400() {
            return new Color(101, 31, 255, 1f);
        }

        @Override
        public Color colorA700() {
            return new Color(98, 0, 234, 1f);
        }

    }

    class Green implements MaterialColor {
        @Override
        public Color color50() {
            return new Color(232, 245, 233, 1f);
        }

        @Override
        public Color color100() {
            return new Color(200, 230, 201, 1f);
        }

        @Override
        public Color color200() {
            return new Color(165, 214, 167, 1f);
        }

        @Override
        public Color color300() {
            return new Color(129, 199, 132, 1f);
        }

        @Override
        public Color color400() {
            return new Color(102, 187, 106, 1f);
        }

        @Override
        public Color color500() {
            return new Color(76, 175, 80, 1f);
        }

        @Override
        public Color color600() {
            return new Color(67, 160, 71, 1f);
        }

        @Override
        public Color color700() {
            return new Color(56, 142, 60, 1f);
        }

        @Override
        public Color color800() {
            return new Color(46, 125, 50, 1f);
        }

        @Override
        public Color color900() {
            return new Color(27, 94, 32, 1f);
        }

        @Override
        public Color colorA100() {
            return new Color(185, 246, 202, 1f);
        }

        @Override
        public Color colorA200() {
            return new Color(105, 240, 174, 1f);
        }

        @Override
        public Color colorA400() {
            return new Color(0, 230, 118, 1f);
        }

        @Override
        public Color colorA700() {
            return new Color(0, 200, 83, 1f);
        }

    }

    class Indigo implements MaterialColor {
        @Override
        public Color color50() {
            return new Color(232, 234, 246, 1f);
        }

        @Override
        public Color color100() {
            return new Color(197, 202, 233, 1f);
        }

        @Override
        public Color color200() {
            return new Color(159, 168, 218, 1f);
        }

        @Override
        public Color color300() {
            return new Color(121, 134, 203, 1f);
        }

        @Override
        public Color color400() {
            return new Color(92, 107, 192, 1f);
        }

        @Override
        public Color color500() {
            return new Color(63, 81, 181, 1f);
        }

        @Override
        public Color color600() {
            return new Color(57, 73, 171, 1f);
        }

        @Override
        public Color color700() {
            return new Color(48, 63, 159, 1f);
        }

        @Override
        public Color color800() {
            return new Color(40, 53, 147, 1f);
        }

        @Override
        public Color color900() {
            return new Color(26, 35, 126, 1f);
        }

        @Override
        public Color colorA100() {
            return new Color(140, 158, 255, 1f);
        }

        @Override
        public Color colorA200() {
            return new Color(83, 109, 254, 1f);
        }

        @Override
        public Color colorA400() {
            return new Color(61, 90, 254, 1f);
        }

        @Override
        public Color colorA700() {
            return new Color(48, 79, 254, 1f);
        }

    }

    class LightBlue implements MaterialColor {
        @Override
        public Color color50() {
            return new Color(225, 245, 254, 1f);
        }

        @Override
        public Color color100() {
            return new Color(179, 229, 252, 1f);
        }

        @Override
        public Color color200() {
            return new Color(129, 212, 250, 1f);
        }

        @Override
        public Color color300() {
            return new Color(79, 195, 247, 1f);
        }

        @Override
        public Color color400() {
            return new Color(41, 182, 246, 1f);
        }

        @Override
        public Color color500() {
            return new Color(3, 169, 244, 1f);
        }

        @Override
        public Color color600() {
            return new Color(3, 155, 229, 1f);
        }

        @Override
        public Color color700() {
            return new Color(2, 136, 209, 1f);
        }

        @Override
        public Color color800() {
            return new Color(2, 119, 189, 1f);
        }

        @Override
        public Color color900() {
            return new Color(1, 87, 155, 1f);
        }

        @Override
        public Color colorA100() {
            return new Color(128, 216, 255, 1f);
        }

        @Override
        public Color colorA200() {
            return new Color(64, 196, 255, 1f);
        }

        @Override
        public Color colorA400() {
            return new Color(0, 176, 255, 1f);
        }

        @Override
        public Color colorA700() {
            return new Color(0, 145, 234, 1f);
        }

    }

    class LightGreen implements MaterialColor {
        @Override
        public Color color50() {
            return new Color(241, 248, 233, 1f);
        }

        @Override
        public Color color100() {
            return new Color(220, 237, 200, 1f);
        }

        @Override
        public Color color200() {
            return new Color(197, 225, 165, 1f);
        }

        @Override
        public Color color300() {
            return new Color(174, 213, 129, 1f);
        }

        @Override
        public Color color400() {
            return new Color(156, 204, 101, 1f);
        }

        @Override
        public Color color500() {
            return new Color(139, 195, 74, 1f);
        }

        @Override
        public Color color600() {
            return new Color(124, 179, 66, 1f);
        }

        @Override
        public Color color700() {
            return new Color(104, 159, 56, 1f);
        }

        @Override
        public Color color800() {
            return new Color(85, 139, 47, 1f);
        }

        @Override
        public Color color900() {
            return new Color(51, 105, 30, 1f);
        }

        @Override
        public Color colorA100() {
            return new Color(204, 255, 144, 1f);
        }

        @Override
        public Color colorA200() {
            return new Color(178, 255, 89, 1f);
        }

        @Override
        public Color colorA400() {
            return new Color(118, 255, 3, 1f);
        }

        @Override
        public Color colorA700() {
            return new Color(100, 221, 23, 1f);
        }

    }

    class Lime implements MaterialColor {
        @Override
        public Color color50() {
            return new Color(249, 251, 231, 1f);
        }

        @Override
        public Color color100() {
            return new Color(240, 244, 195, 1f);
        }

        @Override
        public Color color200() {
            return new Color(230, 238, 156, 1f);
        }

        @Override
        public Color color300() {
            return new Color(220, 231, 117, 1f);
        }

        @Override
        public Color color400() {
            return new Color(212, 225, 87, 1f);
        }

        @Override
        public Color color500() {
            return new Color(205, 220, 57, 1f);
        }

        @Override
        public Color color600() {
            return new Color(192, 202, 51, 1f);
        }

        @Override
        public Color color700() {
            return new Color(175, 180, 43, 1f);
        }

        @Override
        public Color color800() {
            return new Color(158, 157, 36, 1f);
        }

        @Override
        public Color color900() {
            return new Color(130, 119, 23, 1f);
        }

        @Override
        public Color colorA100() {
            return new Color(244, 255, 129, 1f);
        }

        @Override
        public Color colorA200() {
            return new Color(238, 255, 65, 1f);
        }

        @Override
        public Color colorA400() {
            return new Color(198, 255, 0, 1f);
        }

        @Override
        public Color colorA700() {
            return new Color(174, 234, 0, 1f);
        }

    }

    class Orange implements MaterialColor {
        @Override
        public Color color50() {
            return new Color(255, 243, 224, 1f);
        }

        @Override
        public Color color100() {
            return new Color(255, 224, 178, 1f);
        }

        @Override
        public Color color200() {
            return new Color(255, 204, 128, 1f);
        }

        @Override
        public Color color300() {
            return new Color(255, 183, 77, 1f);
        }

        @Override
        public Color color400() {
            return new Color(255, 167, 38, 1f);
        }

        @Override
        public Color color500() {
            return new Color(255, 152, 0, 1f);
        }

        @Override
        public Color color600() {
            return new Color(251, 140, 0, 1f);
        }

        @Override
        public Color color700() {
            return new Color(245, 124, 0, 1f);
        }

        @Override
        public Color color800() {
            return new Color(239, 108, 0, 1f);
        }

        @Override
        public Color color900() {
            return new Color(230, 81, 0, 1f);
        }

        @Override
        public Color colorA100() {
            return new Color(255, 209, 128, 1f);
        }

        @Override
        public Color colorA200() {
            return new Color(255, 171, 64, 1f);
        }

        @Override
        public Color colorA400() {
            return new Color(255, 145, 0, 1f);
        }

        @Override
        public Color colorA700() {
            return new Color(255, 109, 0, 1f);
        }

    }

    class Pink implements MaterialColor {
        @Override
        public Color color50() {
            return new Color(252, 228, 236, 1f);
        }

        @Override
        public Color color100() {
            return new Color(248, 187, 208, 1f);
        }

        @Override
        public Color color200() {
            return new Color(244, 143, 177, 1f);
        }

        @Override
        public Color color300() {
            return new Color(240, 98, 146, 1f);
        }

        @Override
        public Color color400() {
            return new Color(236, 64, 122, 1f);
        }

        @Override
        public Color color500() {
            return new Color(233, 30, 99, 1f);
        }

        @Override
        public Color color600() {
            return new Color(216, 27, 96, 1f);
        }

        @Override
        public Color color700() {
            return new Color(194, 24, 91, 1f);
        }

        @Override
        public Color color800() {
            return new Color(173, 20, 87, 1f);
        }

        @Override
        public Color color900() {
            return new Color(136, 14, 79, 1f);
        }

        @Override
        public Color colorA100() {
            return new Color(255, 128, 171, 1f);
        }

        @Override
        public Color colorA200() {
            return new Color(255, 64, 129, 1f);
        }

        @Override
        public Color colorA400() {
            return new Color(245, 0, 87, 1f);
        }

        @Override
        public Color colorA700() {
            return new Color(197, 17, 98, 1f);
        }

    }

    class Purple implements MaterialColor {
        @Override
        public Color color50() {
            return new Color(243, 229, 245, 1f);
        }

        @Override
        public Color color100() {
            return new Color(225, 190, 231, 1f);
        }

        @Override
        public Color color200() {
            return new Color(206, 147, 216, 1f);
        }

        @Override
        public Color color300() {
            return new Color(186, 104, 200, 1f);
        }

        @Override
        public Color color400() {
            return new Color(171, 71, 188, 1f);
        }

        @Override
        public Color color500() {
            return new Color(156, 39, 176, 1f);
        }

        @Override
        public Color color600() {
            return new Color(142, 36, 170, 1f);
        }

        @Override
        public Color color700() {
            return new Color(123, 31, 162, 1f);
        }

        @Override
        public Color color800() {
            return new Color(106, 27, 154, 1f);
        }

        @Override
        public Color color900() {
            return new Color(74, 20, 140, 1f);
        }

        @Override
        public Color colorA100() {
            return new Color(234, 128, 252, 1f);
        }

        @Override
        public Color colorA200() {
            return new Color(224, 64, 251, 1f);
        }

        @Override
        public Color colorA400() {
            return new Color(213, 0, 249, 1f);
        }

        @Override
        public Color colorA700() {
            return new Color(170, 0, 255, 1f);
        }

    }

    class Red implements MaterialColor {
        @Override
        public Color color50() {
            return new Color(255, 235, 238, 1f);
        }

        @Override
        public Color color100() {
            return new Color(255, 205, 210, 1f);
        }

        @Override
        public Color color200() {
            return new Color(239, 154, 154, 1f);
        }

        @Override
        public Color color300() {
            return new Color(229, 115, 115, 1f);
        }

        @Override
        public Color color400() {
            return new Color(239, 83, 80, 1f);
        }

        @Override
        public Color color500() {
            return new Color(244, 67, 54, 1f);
        }

        @Override
        public Color color600() {
            return new Color(229, 57, 53, 1f);
        }

        @Override
        public Color color700() {
            return new Color(211, 47, 47, 1f);
        }

        @Override
        public Color color800() {
            return new Color(198, 40, 40, 1f);
        }

        @Override
        public Color color900() {
            return new Color(183, 28, 28, 1f);
        }

        @Override
        public Color colorA100() {
            return new Color(255, 138, 128, 1f);
        }

        @Override
        public Color colorA200() {
            return new Color(255, 82, 82, 1f);
        }

        @Override
        public Color colorA400() {
            return new Color(255, 23, 68, 1f);
        }

        @Override
        public Color colorA700() {
            return new Color(213, 0, 0, 1f);
        }

    }

    class Teal implements MaterialColor {
        @Override
        public Color color50() {
            return new Color(224, 242, 241, 1f);
        }

        @Override
        public Color color100() {
            return new Color(178, 223, 219, 1f);
        }

        @Override
        public Color color200() {
            return new Color(128, 203, 196, 1f);
        }

        @Override
        public Color color300() {
            return new Color(77, 182, 172, 1f);
        }

        @Override
        public Color color400() {
            return new Color(38, 166, 154, 1f);
        }

        @Override
        public Color color500() {
            return new Color(0, 150, 136, 1f);
        }

        @Override
        public Color color600() {
            return new Color(0, 137, 123, 1f);
        }

        @Override
        public Color color700() {
            return new Color(0, 121, 107, 1f);
        }

        @Override
        public Color color800() {
            return new Color(0, 105, 92, 1f);
        }

        @Override
        public Color color900() {
            return new Color(0, 77, 64, 1f);
        }

        @Override
        public Color colorA100() {
            return new Color(167, 255, 235, 1f);
        }

        @Override
        public Color colorA200() {
            return new Color(100, 255, 218, 1f);
        }

        @Override
        public Color colorA400() {
            return new Color(29, 233, 182, 1f);
        }

        @Override
        public Color colorA700() {
            return new Color(0, 191, 165, 1f);
        }

    }

    class Yellow implements MaterialColor {
        @Override
        public Color color50() {
            return new Color(255, 253, 231, 1f);
        }

        @Override
        public Color color100() {
            return new Color(255, 249, 196, 1f);
        }

        @Override
        public Color color200() {
            return new Color(255, 245, 157, 1f);
        }

        @Override
        public Color color300() {
            return new Color(255, 241, 118, 1f);
        }

        @Override
        public Color color400() {
            return new Color(255, 238, 88, 1f);
        }

        @Override
        public Color color500() {
            return new Color(255, 235, 59, 1f);
        }

        @Override
        public Color color600() {
            return new Color(253, 216, 53, 1f);
        }

        @Override
        public Color color700() {
            return new Color(251, 192, 45, 1f);
        }

        @Override
        public Color color800() {
            return new Color(249, 168, 37, 1f);
        }

        @Override
        public Color color900() {
            return new Color(245, 127, 23, 1f);
        }

        @Override
        public Color colorA100() {
            return new Color(255, 255, 141, 1f);
        }

        @Override
        public Color colorA200() {
            return new Color(255, 255, 0, 1f);
        }

        @Override
        public Color colorA400() {
            return new Color(255, 234, 0, 1f);
        }

        @Override
        public Color colorA700() {
            return new Color(255, 214, 0, 1f);
        }

    }
}
