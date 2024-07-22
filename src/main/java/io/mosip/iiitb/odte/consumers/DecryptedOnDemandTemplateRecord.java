package io.mosip.iiitb.odte.consumers;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = false)
@Data
public class DecryptedOnDemandTemplateRecord extends DecryptedRecord {
    private String id;
    private String idType;
}