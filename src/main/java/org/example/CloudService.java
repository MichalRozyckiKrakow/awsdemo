package org.example;

import org.example.model.CloudInfo;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.regions.internal.util.EC2MetadataUtils;

@Service
public class CloudService {

    public CloudInfo getCloudInfo() {
        return new CloudInfo(
            EC2MetadataUtils.getEC2InstanceRegion(),
            EC2MetadataUtils.getAvailabilityZone());
    }


}