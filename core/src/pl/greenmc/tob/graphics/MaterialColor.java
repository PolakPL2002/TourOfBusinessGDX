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
            return new Color(255 / 255f, 248 / 255f, 225 / 255f, 1f);
        }

        @Override
        public Color color100() {
            return new Color(255 / 255f, 236 / 255f, 179 / 255f, 1f);
        }

        @Override
        public Color color200() {
            return new Color(255 / 255f, 224 / 255f, 130 / 255f, 1f);
        }

        @Override
        public Color color300() {
            return new Color(255 / 255f, 213 / 255f, 79 / 255f, 1f);
        }

        @Override
        public Color color400() {
            return new Color(255 / 255f, 202 / 255f, 40 / 255f, 1f);
        }

        @Override
        public Color color500() {
            return new Color(255 / 255f, 193 / 255f, 7 / 255f, 1f);
        }

        @Override
        public Color color600() {
            return new Color(255 / 255f, 179 / 255f, 0 / 255f, 1f);
        }

        @Override
        public Color color700() {
            return new Color(255 / 255f, 160 / 255f, 0 / 255f, 1f);
        }

        @Override
        public Color color800() {
            return new Color(255 / 255f, 143 / 255f, 0 / 255f, 1f);
        }

        @Override
        public Color color900() {
            return new Color(255 / 255f, 111 / 255f, 0 / 255f, 1f);
        }

        @Override
        public Color colorA100() {
            return new Color(255 / 255f, 229 / 255f, 127 / 255f, 1f);
        }

        @Override
        public Color colorA200() {
            return new Color(255 / 255f, 215 / 255f, 64 / 255f, 1f);
        }

        @Override
        public Color colorA400() {
            return new Color(255 / 255f, 196 / 255f, 0 / 255f, 1f);
        }

        @Override
        public Color colorA700() {
            return new Color(255 / 255f, 171 / 255f, 0 / 255f, 1f);
        }

    }

    class Blue implements MaterialColor {
        @Override
        public Color color50() {
            return new Color(227 / 255f, 242 / 255f, 253 / 255f, 1f);
        }

        @Override
        public Color color100() {
            return new Color(187 / 255f, 222 / 255f, 251 / 255f, 1f);
        }

        @Override
        public Color color200() {
            return new Color(144 / 255f, 202 / 255f, 249 / 255f, 1f);
        }

        @Override
        public Color color300() {
            return new Color(100 / 255f, 181 / 255f, 246 / 255f, 1f);
        }

        @Override
        public Color color400() {
            return new Color(66 / 255f, 165 / 255f, 245 / 255f, 1f);
        }

        @Override
        public Color color500() {
            return new Color(33 / 255f, 150 / 255f, 243 / 255f, 1f);
        }

        @Override
        public Color color600() {
            return new Color(30 / 255f, 136 / 255f, 229 / 255f, 1f);
        }

        @Override
        public Color color700() {
            return new Color(25 / 255f, 118 / 255f, 210 / 255f, 1f);
        }

        @Override
        public Color color800() {
            return new Color(21 / 255f, 101 / 255f, 192 / 255f, 1f);
        }

        @Override
        public Color color900() {
            return new Color(13 / 255f, 71 / 255f, 161 / 255f, 1f);
        }

        @Override
        public Color colorA100() {
            return new Color(130 / 255f, 177 / 255f, 255 / 255f, 1f);
        }

        @Override
        public Color colorA200() {
            return new Color(68 / 255f, 138 / 255f, 255 / 255f, 1f);
        }

        @Override
        public Color colorA400() {
            return new Color(41 / 255f, 121 / 255f, 255 / 255f, 1f);
        }

        @Override
        public Color colorA700() {
            return new Color(41 / 255f, 98 / 255f, 255 / 255f, 1f);
        }

    }

    class Cyan implements MaterialColor {
        @Override
        public Color color50() {
            return new Color(224 / 255f, 247 / 255f, 250 / 255f, 1f);
        }

        @Override
        public Color color100() {
            return new Color(178 / 255f, 235 / 255f, 242 / 255f, 1f);
        }

        @Override
        public Color color200() {
            return new Color(128 / 255f, 222 / 255f, 234 / 255f, 1f);
        }

        @Override
        public Color color300() {
            return new Color(77 / 255f, 208 / 255f, 225 / 255f, 1f);
        }

        @Override
        public Color color400() {
            return new Color(38 / 255f, 198 / 255f, 218 / 255f, 1f);
        }

        @Override
        public Color color500() {
            return new Color(0 / 255f, 188 / 255f, 212 / 255f, 1f);
        }

        @Override
        public Color color600() {
            return new Color(0 / 255f, 172 / 255f, 193 / 255f, 1f);
        }

        @Override
        public Color color700() {
            return new Color(0 / 255f, 151 / 255f, 167 / 255f, 1f);
        }

        @Override
        public Color color800() {
            return new Color(0 / 255f, 131 / 255f, 143 / 255f, 1f);
        }

        @Override
        public Color color900() {
            return new Color(0 / 255f, 96 / 255f, 100 / 255f, 1f);
        }

        @Override
        public Color colorA100() {
            return new Color(132 / 255f, 255 / 255f, 255 / 255f, 1f);
        }

        @Override
        public Color colorA200() {
            return new Color(24 / 255f, 255 / 255f, 255 / 255f, 1f);
        }

        @Override
        public Color colorA400() {
            return new Color(0 / 255f, 229 / 255f, 255 / 255f, 1f);
        }

        @Override
        public Color colorA700() {
            return new Color(0 / 255f, 184 / 255f, 212 / 255f, 1f);
        }

    }

    class DeepOrange implements MaterialColor {
        @Override
        public Color color50() {
            return new Color(251 / 255f, 233 / 255f, 231 / 255f, 1f);
        }

        @Override
        public Color color100() {
            return new Color(255 / 255f, 204 / 255f, 188 / 255f, 1f);
        }

        @Override
        public Color color200() {
            return new Color(255 / 255f, 171 / 255f, 145 / 255f, 1f);
        }

        @Override
        public Color color300() {
            return new Color(255 / 255f, 138 / 255f, 101 / 255f, 1f);
        }

        @Override
        public Color color400() {
            return new Color(255 / 255f, 112 / 255f, 67 / 255f, 1f);
        }

        @Override
        public Color color500() {
            return new Color(255 / 255f, 87 / 255f, 34 / 255f, 1f);
        }

        @Override
        public Color color600() {
            return new Color(244 / 255f, 81 / 255f, 30 / 255f, 1f);
        }

        @Override
        public Color color700() {
            return new Color(230 / 255f, 74 / 255f, 25 / 255f, 1f);
        }

        @Override
        public Color color800() {
            return new Color(216 / 255f, 67 / 255f, 21 / 255f, 1f);
        }

        @Override
        public Color color900() {
            return new Color(191 / 255f, 54 / 255f, 12 / 255f, 1f);
        }

        @Override
        public Color colorA100() {
            return new Color(255 / 255f, 158 / 255f, 128 / 255f, 1f);
        }

        @Override
        public Color colorA200() {
            return new Color(255 / 255f, 110 / 255f, 64 / 255f, 1f);
        }

        @Override
        public Color colorA400() {
            return new Color(255 / 255f, 61 / 255f, 0 / 255f, 1f);
        }

        @Override
        public Color colorA700() {
            return new Color(221 / 255f, 44 / 255f, 0 / 255f, 1f);
        }

    }

    class DeepPurple implements MaterialColor {
        @Override
        public Color color50() {
            return new Color(237 / 255f, 231 / 255f, 246 / 255f, 1f);
        }

        @Override
        public Color color100() {
            return new Color(209 / 255f, 196 / 255f, 233 / 255f, 1f);
        }

        @Override
        public Color color200() {
            return new Color(179 / 255f, 157 / 255f, 219 / 255f, 1f);
        }

        @Override
        public Color color300() {
            return new Color(149 / 255f, 117 / 255f, 205 / 255f, 1f);
        }

        @Override
        public Color color400() {
            return new Color(126 / 255f, 87 / 255f, 194 / 255f, 1f);
        }

        @Override
        public Color color500() {
            return new Color(103 / 255f, 58 / 255f, 183 / 255f, 1f);
        }

        @Override
        public Color color600() {
            return new Color(94 / 255f, 53 / 255f, 177 / 255f, 1f);
        }

        @Override
        public Color color700() {
            return new Color(81 / 255f, 45 / 255f, 168 / 255f, 1f);
        }

        @Override
        public Color color800() {
            return new Color(69 / 255f, 39 / 255f, 160 / 255f, 1f);
        }

        @Override
        public Color color900() {
            return new Color(49 / 255f, 27 / 255f, 146 / 255f, 1f);
        }

        @Override
        public Color colorA100() {
            return new Color(179 / 255f, 136 / 255f, 255 / 255f, 1f);
        }

        @Override
        public Color colorA200() {
            return new Color(124 / 255f, 77 / 255f, 255 / 255f, 1f);
        }

        @Override
        public Color colorA400() {
            return new Color(101 / 255f, 31 / 255f, 255 / 255f, 1f);
        }

        @Override
        public Color colorA700() {
            return new Color(98 / 255f, 0 / 255f, 234 / 255f, 1f);
        }

    }

    class Green implements MaterialColor {
        @Override
        public Color color50() {
            return new Color(232 / 255f, 245 / 255f, 233 / 255f, 1f);
        }

        @Override
        public Color color100() {
            return new Color(200 / 255f, 230 / 255f, 201 / 255f, 1f);
        }

        @Override
        public Color color200() {
            return new Color(165 / 255f, 214 / 255f, 167 / 255f, 1f);
        }

        @Override
        public Color color300() {
            return new Color(129 / 255f, 199 / 255f, 132 / 255f, 1f);
        }

        @Override
        public Color color400() {
            return new Color(102 / 255f, 187 / 255f, 106 / 255f, 1f);
        }

        @Override
        public Color color500() {
            return new Color(76 / 255f, 175 / 255f, 80 / 255f, 1f);
        }

        @Override
        public Color color600() {
            return new Color(67 / 255f, 160 / 255f, 71 / 255f, 1f);
        }

        @Override
        public Color color700() {
            return new Color(56 / 255f, 142 / 255f, 60 / 255f, 1f);
        }

        @Override
        public Color color800() {
            return new Color(46 / 255f, 125 / 255f, 50 / 255f, 1f);
        }

        @Override
        public Color color900() {
            return new Color(27 / 255f, 94 / 255f, 32 / 255f, 1f);
        }

        @Override
        public Color colorA100() {
            return new Color(185 / 255f, 246 / 255f, 202 / 255f, 1f);
        }

        @Override
        public Color colorA200() {
            return new Color(105 / 255f, 240 / 255f, 174 / 255f, 1f);
        }

        @Override
        public Color colorA400() {
            return new Color(0 / 255f, 230 / 255f, 118 / 255f, 1f);
        }

        @Override
        public Color colorA700() {
            return new Color(0 / 255f, 200 / 255f, 83 / 255f, 1f);
        }

    }

    class Indigo implements MaterialColor {
        @Override
        public Color color50() {
            return new Color(232 / 255f, 234 / 255f, 246 / 255f, 1f);
        }

        @Override
        public Color color100() {
            return new Color(197 / 255f, 202 / 255f, 233 / 255f, 1f);
        }

        @Override
        public Color color200() {
            return new Color(159 / 255f, 168 / 255f, 218 / 255f, 1f);
        }

        @Override
        public Color color300() {
            return new Color(121 / 255f, 134 / 255f, 203 / 255f, 1f);
        }

        @Override
        public Color color400() {
            return new Color(92 / 255f, 107 / 255f, 192 / 255f, 1f);
        }

        @Override
        public Color color500() {
            return new Color(63 / 255f, 81 / 255f, 181 / 255f, 1f);
        }

        @Override
        public Color color600() {
            return new Color(57 / 255f, 73 / 255f, 171 / 255f, 1f);
        }

        @Override
        public Color color700() {
            return new Color(48 / 255f, 63 / 255f, 159 / 255f, 1f);
        }

        @Override
        public Color color800() {
            return new Color(40 / 255f, 53 / 255f, 147 / 255f, 1f);
        }

        @Override
        public Color color900() {
            return new Color(26 / 255f, 35 / 255f, 126 / 255f, 1f);
        }

        @Override
        public Color colorA100() {
            return new Color(140 / 255f, 158 / 255f, 255 / 255f, 1f);
        }

        @Override
        public Color colorA200() {
            return new Color(83 / 255f, 109 / 255f, 254 / 255f, 1f);
        }

        @Override
        public Color colorA400() {
            return new Color(61 / 255f, 90 / 255f, 254 / 255f, 1f);
        }

        @Override
        public Color colorA700() {
            return new Color(48 / 255f, 79 / 255f, 254 / 255f, 1f);
        }

    }

    class LightBlue implements MaterialColor {
        @Override
        public Color color50() {
            return new Color(225 / 255f, 245 / 255f, 254 / 255f, 1f);
        }

        @Override
        public Color color100() {
            return new Color(179 / 255f, 229 / 255f, 252 / 255f, 1f);
        }

        @Override
        public Color color200() {
            return new Color(129 / 255f, 212 / 255f, 250 / 255f, 1f);
        }

        @Override
        public Color color300() {
            return new Color(79 / 255f, 195 / 255f, 247 / 255f, 1f);
        }

        @Override
        public Color color400() {
            return new Color(41 / 255f, 182 / 255f, 246 / 255f, 1f);
        }

        @Override
        public Color color500() {
            return new Color(3 / 255f, 169 / 255f, 244 / 255f, 1f);
        }

        @Override
        public Color color600() {
            return new Color(3 / 255f, 155 / 255f, 229 / 255f, 1f);
        }

        @Override
        public Color color700() {
            return new Color(2 / 255f, 136 / 255f, 209 / 255f, 1f);
        }

        @Override
        public Color color800() {
            return new Color(2 / 255f, 119 / 255f, 189 / 255f, 1f);
        }

        @Override
        public Color color900() {
            return new Color(1 / 255f, 87 / 255f, 155 / 255f, 1f);
        }

        @Override
        public Color colorA100() {
            return new Color(128 / 255f, 216 / 255f, 255 / 255f, 1f);
        }

        @Override
        public Color colorA200() {
            return new Color(64 / 255f, 196 / 255f, 255 / 255f, 1f);
        }

        @Override
        public Color colorA400() {
            return new Color(0 / 255f, 176 / 255f, 255 / 255f, 1f);
        }

        @Override
        public Color colorA700() {
            return new Color(0 / 255f, 145 / 255f, 234 / 255f, 1f);
        }

    }

    class LightGreen implements MaterialColor {
        @Override
        public Color color50() {
            return new Color(241 / 255f, 248 / 255f, 233 / 255f, 1f);
        }

        @Override
        public Color color100() {
            return new Color(220 / 255f, 237 / 255f, 200 / 255f, 1f);
        }

        @Override
        public Color color200() {
            return new Color(197 / 255f, 225 / 255f, 165 / 255f, 1f);
        }

        @Override
        public Color color300() {
            return new Color(174 / 255f, 213 / 255f, 129 / 255f, 1f);
        }

        @Override
        public Color color400() {
            return new Color(156 / 255f, 204 / 255f, 101 / 255f, 1f);
        }

        @Override
        public Color color500() {
            return new Color(139 / 255f, 195 / 255f, 74 / 255f, 1f);
        }

        @Override
        public Color color600() {
            return new Color(124 / 255f, 179 / 255f, 66 / 255f, 1f);
        }

        @Override
        public Color color700() {
            return new Color(104 / 255f, 159 / 255f, 56 / 255f, 1f);
        }

        @Override
        public Color color800() {
            return new Color(85 / 255f, 139 / 255f, 47 / 255f, 1f);
        }

        @Override
        public Color color900() {
            return new Color(51 / 255f, 105 / 255f, 30 / 255f, 1f);
        }

        @Override
        public Color colorA100() {
            return new Color(204 / 255f, 255 / 255f, 144 / 255f, 1f);
        }

        @Override
        public Color colorA200() {
            return new Color(178 / 255f, 255 / 255f, 89 / 255f, 1f);
        }

        @Override
        public Color colorA400() {
            return new Color(118 / 255f, 255 / 255f, 3 / 255f, 1f);
        }

        @Override
        public Color colorA700() {
            return new Color(100 / 255f, 221 / 255f, 23 / 255f, 1f);
        }

    }

    class Lime implements MaterialColor {
        @Override
        public Color color50() {
            return new Color(249 / 255f, 251 / 255f, 231 / 255f, 1f);
        }

        @Override
        public Color color100() {
            return new Color(240 / 255f, 244 / 255f, 195 / 255f, 1f);
        }

        @Override
        public Color color200() {
            return new Color(230 / 255f, 238 / 255f, 156 / 255f, 1f);
        }

        @Override
        public Color color300() {
            return new Color(220 / 255f, 231 / 255f, 117 / 255f, 1f);
        }

        @Override
        public Color color400() {
            return new Color(212 / 255f, 225 / 255f, 87 / 255f, 1f);
        }

        @Override
        public Color color500() {
            return new Color(205 / 255f, 220 / 255f, 57 / 255f, 1f);
        }

        @Override
        public Color color600() {
            return new Color(192 / 255f, 202 / 255f, 51 / 255f, 1f);
        }

        @Override
        public Color color700() {
            return new Color(175 / 255f, 180 / 255f, 43 / 255f, 1f);
        }

        @Override
        public Color color800() {
            return new Color(158 / 255f, 157 / 255f, 36 / 255f, 1f);
        }

        @Override
        public Color color900() {
            return new Color(130 / 255f, 119 / 255f, 23 / 255f, 1f);
        }

        @Override
        public Color colorA100() {
            return new Color(244 / 255f, 255 / 255f, 129 / 255f, 1f);
        }

        @Override
        public Color colorA200() {
            return new Color(238 / 255f, 255 / 255f, 65 / 255f, 1f);
        }

        @Override
        public Color colorA400() {
            return new Color(198 / 255f, 255 / 255f, 0 / 255f, 1f);
        }

        @Override
        public Color colorA700() {
            return new Color(174 / 255f, 234 / 255f, 0 / 255f, 1f);
        }

    }

    class Orange implements MaterialColor {
        @Override
        public Color color50() {
            return new Color(255 / 255f, 243 / 255f, 224 / 255f, 1f);
        }

        @Override
        public Color color100() {
            return new Color(255 / 255f, 224 / 255f, 178 / 255f, 1f);
        }

        @Override
        public Color color200() {
            return new Color(255 / 255f, 204 / 255f, 128 / 255f, 1f);
        }

        @Override
        public Color color300() {
            return new Color(255 / 255f, 183 / 255f, 77 / 255f, 1f);
        }

        @Override
        public Color color400() {
            return new Color(255 / 255f, 167 / 255f, 38 / 255f, 1f);
        }

        @Override
        public Color color500() {
            return new Color(255 / 255f, 152 / 255f, 0 / 255f, 1f);
        }

        @Override
        public Color color600() {
            return new Color(251 / 255f, 140 / 255f, 0 / 255f, 1f);
        }

        @Override
        public Color color700() {
            return new Color(245 / 255f, 124 / 255f, 0 / 255f, 1f);
        }

        @Override
        public Color color800() {
            return new Color(239 / 255f, 108 / 255f, 0 / 255f, 1f);
        }

        @Override
        public Color color900() {
            return new Color(230 / 255f, 81 / 255f, 0 / 255f, 1f);
        }

        @Override
        public Color colorA100() {
            return new Color(255 / 255f, 209 / 255f, 128 / 255f, 1f);
        }

        @Override
        public Color colorA200() {
            return new Color(255 / 255f, 171 / 255f, 64 / 255f, 1f);
        }

        @Override
        public Color colorA400() {
            return new Color(255 / 255f, 145 / 255f, 0 / 255f, 1f);
        }

        @Override
        public Color colorA700() {
            return new Color(255 / 255f, 109 / 255f, 0 / 255f, 1f);
        }

    }

    class Pink implements MaterialColor {
        @Override
        public Color color50() {
            return new Color(252 / 255f, 228 / 255f, 236 / 255f, 1f);
        }

        @Override
        public Color color100() {
            return new Color(248 / 255f, 187 / 255f, 208 / 255f, 1f);
        }

        @Override
        public Color color200() {
            return new Color(244 / 255f, 143 / 255f, 177 / 255f, 1f);
        }

        @Override
        public Color color300() {
            return new Color(240 / 255f, 98 / 255f, 146 / 255f, 1f);
        }

        @Override
        public Color color400() {
            return new Color(236 / 255f, 64 / 255f, 122 / 255f, 1f);
        }

        @Override
        public Color color500() {
            return new Color(233 / 255f, 30 / 255f, 99 / 255f, 1f);
        }

        @Override
        public Color color600() {
            return new Color(216 / 255f, 27 / 255f, 96 / 255f, 1f);
        }

        @Override
        public Color color700() {
            return new Color(194 / 255f, 24 / 255f, 91 / 255f, 1f);
        }

        @Override
        public Color color800() {
            return new Color(173 / 255f, 20 / 255f, 87 / 255f, 1f);
        }

        @Override
        public Color color900() {
            return new Color(136 / 255f, 14 / 255f, 79 / 255f, 1f);
        }

        @Override
        public Color colorA100() {
            return new Color(255 / 255f, 128 / 255f, 171 / 255f, 1f);
        }

        @Override
        public Color colorA200() {
            return new Color(255 / 255f, 64 / 255f, 129 / 255f, 1f);
        }

        @Override
        public Color colorA400() {
            return new Color(245 / 255f, 0 / 255f, 87 / 255f, 1f);
        }

        @Override
        public Color colorA700() {
            return new Color(197 / 255f, 17 / 255f, 98 / 255f, 1f);
        }

    }

    class Purple implements MaterialColor {
        @Override
        public Color color50() {
            return new Color(243 / 255f, 229 / 255f, 245 / 255f, 1f);
        }

        @Override
        public Color color100() {
            return new Color(225 / 255f, 190 / 255f, 231 / 255f, 1f);
        }

        @Override
        public Color color200() {
            return new Color(206 / 255f, 147 / 255f, 216 / 255f, 1f);
        }

        @Override
        public Color color300() {
            return new Color(186 / 255f, 104 / 255f, 200 / 255f, 1f);
        }

        @Override
        public Color color400() {
            return new Color(171 / 255f, 71 / 255f, 188 / 255f, 1f);
        }

        @Override
        public Color color500() {
            return new Color(156 / 255f, 39 / 255f, 176 / 255f, 1f);
        }

        @Override
        public Color color600() {
            return new Color(142 / 255f, 36 / 255f, 170 / 255f, 1f);
        }

        @Override
        public Color color700() {
            return new Color(123 / 255f, 31 / 255f, 162 / 255f, 1f);
        }

        @Override
        public Color color800() {
            return new Color(106 / 255f, 27 / 255f, 154 / 255f, 1f);
        }

        @Override
        public Color color900() {
            return new Color(74 / 255f, 20 / 255f, 140 / 255f, 1f);
        }

        @Override
        public Color colorA100() {
            return new Color(234 / 255f, 128 / 255f, 252 / 255f, 1f);
        }

        @Override
        public Color colorA200() {
            return new Color(224 / 255f, 64 / 255f, 251 / 255f, 1f);
        }

        @Override
        public Color colorA400() {
            return new Color(213 / 255f, 0 / 255f, 249 / 255f, 1f);
        }

        @Override
        public Color colorA700() {
            return new Color(170 / 255f, 0 / 255f, 255 / 255f, 1f);
        }

    }

    class Red implements MaterialColor {
        @Override
        public Color color50() {
            return new Color(255 / 255f, 235 / 255f, 238 / 255f, 1f);
        }

        @Override
        public Color color100() {
            return new Color(255 / 255f, 205 / 255f, 210 / 255f, 1f);
        }

        @Override
        public Color color200() {
            return new Color(239 / 255f, 154 / 255f, 154 / 255f, 1f);
        }

        @Override
        public Color color300() {
            return new Color(229 / 255f, 115 / 255f, 115 / 255f, 1f);
        }

        @Override
        public Color color400() {
            return new Color(239 / 255f, 83 / 255f, 80 / 255f, 1f);
        }

        @Override
        public Color color500() {
            return new Color(244 / 255f, 67 / 255f, 54 / 255f, 1f);
        }

        @Override
        public Color color600() {
            return new Color(229 / 255f, 57 / 255f, 53 / 255f, 1f);
        }

        @Override
        public Color color700() {
            return new Color(211 / 255f, 47 / 255f, 47 / 255f, 1f);
        }

        @Override
        public Color color800() {
            return new Color(198 / 255f, 40 / 255f, 40 / 255f, 1f);
        }

        @Override
        public Color color900() {
            return new Color(183 / 255f, 28 / 255f, 28 / 255f, 1f);
        }

        @Override
        public Color colorA100() {
            return new Color(255 / 255f, 138 / 255f, 128 / 255f, 1f);
        }

        @Override
        public Color colorA200() {
            return new Color(255 / 255f, 82 / 255f, 82 / 255f, 1f);
        }

        @Override
        public Color colorA400() {
            return new Color(255 / 255f, 23 / 255f, 68 / 255f, 1f);
        }

        @Override
        public Color colorA700() {
            return new Color(213 / 255f, 0 / 255f, 0 / 255f, 1f);
        }

    }

    class Teal implements MaterialColor {
        @Override
        public Color color50() {
            return new Color(224 / 255f, 242 / 255f, 241 / 255f, 1f);
        }

        @Override
        public Color color100() {
            return new Color(178 / 255f, 223 / 255f, 219 / 255f, 1f);
        }

        @Override
        public Color color200() {
            return new Color(128 / 255f, 203 / 255f, 196 / 255f, 1f);
        }

        @Override
        public Color color300() {
            return new Color(77 / 255f, 182 / 255f, 172 / 255f, 1f);
        }

        @Override
        public Color color400() {
            return new Color(38 / 255f, 166 / 255f, 154 / 255f, 1f);
        }

        @Override
        public Color color500() {
            return new Color(0 / 255f, 150 / 255f, 136 / 255f, 1f);
        }

        @Override
        public Color color600() {
            return new Color(0 / 255f, 137 / 255f, 123 / 255f, 1f);
        }

        @Override
        public Color color700() {
            return new Color(0 / 255f, 121 / 255f, 107 / 255f, 1f);
        }

        @Override
        public Color color800() {
            return new Color(0 / 255f, 105 / 255f, 92 / 255f, 1f);
        }

        @Override
        public Color color900() {
            return new Color(0 / 255f, 77 / 255f, 64 / 255f, 1f);
        }

        @Override
        public Color colorA100() {
            return new Color(167 / 255f, 255 / 255f, 235 / 255f, 1f);
        }

        @Override
        public Color colorA200() {
            return new Color(100 / 255f, 255 / 255f, 218 / 255f, 1f);
        }

        @Override
        public Color colorA400() {
            return new Color(29 / 255f, 233 / 255f, 182 / 255f, 1f);
        }

        @Override
        public Color colorA700() {
            return new Color(0 / 255f, 191 / 255f, 165 / 255f, 1f);
        }

    }

    class Yellow implements MaterialColor {
        @Override
        public Color color50() {
            return new Color(255 / 255f, 253 / 255f, 231 / 255f, 1f);
        }

        @Override
        public Color color100() {
            return new Color(255 / 255f, 249 / 255f, 196 / 255f, 1f);
        }

        @Override
        public Color color200() {
            return new Color(255 / 255f, 245 / 255f, 157 / 255f, 1f);
        }

        @Override
        public Color color300() {
            return new Color(255 / 255f, 241 / 255f, 118 / 255f, 1f);
        }

        @Override
        public Color color400() {
            return new Color(255 / 255f, 238 / 255f, 88 / 255f, 1f);
        }

        @Override
        public Color color500() {
            return new Color(255 / 255f, 235 / 255f, 59 / 255f, 1f);
        }

        @Override
        public Color color600() {
            return new Color(253 / 255f, 216 / 255f, 53 / 255f, 1f);
        }

        @Override
        public Color color700() {
            return new Color(251 / 255f, 192 / 255f, 45 / 255f, 1f);
        }

        @Override
        public Color color800() {
            return new Color(249 / 255f, 168 / 255f, 37 / 255f, 1f);
        }

        @Override
        public Color color900() {
            return new Color(245 / 255f, 127 / 255f, 23 / 255f, 1f);
        }

        @Override
        public Color colorA100() {
            return new Color(255 / 255f, 255 / 255f, 141 / 255f, 1f);
        }

        @Override
        public Color colorA200() {
            return new Color(255 / 255f, 255 / 255f, 0 / 255f, 1f);
        }

        @Override
        public Color colorA400() {
            return new Color(255 / 255f, 234 / 255f, 0 / 255f, 1f);
        }

        @Override
        public Color colorA700() {
            return new Color(255 / 255f, 214 / 255f, 0 / 255f, 1f);
        }

    }
}
