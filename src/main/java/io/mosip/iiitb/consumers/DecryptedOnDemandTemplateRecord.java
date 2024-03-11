package io.mosip.iiitb.consumers;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = false)
@Data
public class DecryptedOnDemandTemplateRecord extends DecryptedRecord {
    private String id;
}
