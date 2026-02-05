package Forreach.Bunkers;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
public class BunkerConfig {
        public static final BuilderCodec<BunkerConfig>
                CODEC = BuilderCodec.builder(BunkerConfig.class, BunkerConfig::new)
                .append(new KeyedCodec<>("BunkerGenerated", Codec.BOOLEAN),
                        (config, value) -> config.isBunkerGenerated = value, // Setter
                        (config) -> config.isBunkerGenerated).add() // Getter
                .append(new KeyedCodec<>("BunkerPosition", Codec.INT_ARRAY),
                        (config, value) -> config.BunkerPosition = value, // Setter
                        (config) -> config.BunkerPosition).add() // Getter
                .build();
        private boolean isBunkerGenerated = false;
        private int[] BunkerPosition = new int[] {};
        public BunkerConfig() {
        }
        // Getters
        public boolean getBunkerGenerated() {
            return isBunkerGenerated;
        }
        public int[] getBunkerPosition() {
                return BunkerPosition;
        }
        // Setters
        public void setBunkerGenerated(boolean someValue) {
            this.isBunkerGenerated = someValue;
        }
        public void setBunkerPosition(int[] someValue) {
                this.BunkerPosition = someValue;
        }
}
