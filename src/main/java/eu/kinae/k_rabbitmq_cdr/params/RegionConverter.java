package eu.kinae.k_rabbitmq_cdr.params;

import com.beust.jcommander.IStringConverter;
import software.amazon.awssdk.regions.Region;

public class RegionConverter implements IStringConverter<Region> {

    @Override
    public Region convert(String value) {
        return Region.of(value);
    }
}
