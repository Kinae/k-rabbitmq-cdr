package eu.kinae.k_rabbitmq_cdr.params;

import org.junit.jupiter.api.Test;
import software.amazon.awssdk.regions.Region;

import static org.assertj.core.api.Assertions.assertThat;

public class RegionConverterTest {

    @Test
    public void Convert_region() {
        var rc = new RegionConverter();

        String regionId = Region.EU_WEST_1.id();
        Region euWest1 = rc.convert(regionId);
        assertThat(euWest1).isEqualTo(Region.EU_WEST_1);
        assertThat(Region.regions().contains(euWest1)).isTrue();

        Region unknownRegion = rc.convert("unknown-region");
        assertThat(Region.regions().contains(unknownRegion)).isFalse();
    }

}
