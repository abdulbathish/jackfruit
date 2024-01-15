package io.mosip.iiitb.ondemandtemplate.jackfruit.service.impl;

import io.mosip.iiitb.ondemandtemplate.jackfruit.service.OnDemandTemplateSetupService;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

@RefreshScope
@Component
public class OnDemandTemplateSetupServiceImpl implements OnDemandTemplateSetupService {
    public String dummyMethod(String vid) {
        return "";
    }
}

//import java.time.LocalDateTime;
//import java.util.HashMap;
//import java.util.Map;
//import java.util.Optional;
//import java.util.function.IntFunction;

//import org.springframework.cloud.context.config.annotation.RefreshScope;
//import org.springframework.stereotype.Component;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.MediaType;
//import org.springframework.http.ResponseEntity;
//
//import io.mosip.credential.request.generator.entity.CredentialEntity;
//import io.mosip.credential.request.generator.constants.ApiName;
//import io.mosip.credential.request.generator.entity.CredentialEntity;
//import io.mosip.credential.request.generator.service.impl.CredentialRequestServiceImpl;
//import io.mosip.idrepository.core.constant.IdType;
//import io.mosip.idrepository.core.dto.CredentialServiceRequestDto;
//import io.mosip.idrepository.core.dto.ErrorDTO;
//import io.mosip.idrepository.core.security.IdRepoSecurityManager;
//import io.mosip.idrepository.core.spi.IdRepoService;
//import io.mosip.idrepository.core.util.TokenIDGenerator;
//import io.mosip.idrepository.identity.validator.IdRequestValidator;


//@RefreshScope
//@Component
//public class OnDemandTemplateSetupServiceImpl implements OnDemandTemplateSetupService {
//    private IdRequestValidator validator;
//
//    @Autowired
//    private TokenIDGenerator tokenIDGenerator;
//
//    @Autowired
//    private IdRepoSecurityManager securityManager;
//
//    @Autowired
//    private IdRepoService<IdRequestDTO, IdResponseDTO> idRepoService;
//
//    @Autowired
//    private CredentialRequestServiceImpl credentialRequestServiceImpl;
//
//    public CredentialEntity startManualIssuance(
//    		Vid vid
//    ) {
//        if (!validator.validateVid(vid))
//            throw new Error("Not a valid vid");
//
//        String idType = null;
//        Map<String, String> extractionFormats = new HashMap<>();
//        ResponseEntity<IdResponseDTO> responseEntity = idRepoService.retrieveIdentity(
//                id,
//                IdType.VID,
//                idType,
//                extractionFormats
//        );
//        IdResponseDTO idResponseDTO = responseEntity.getBody();
//        if (idResponseDTO == null) {
//        	throw new Error("no idResponse was found");
//        }
//
//        String uin = idResponseDTO.getUin();
//        String partnerId = "mpartner-default-auth";
//        LocalDateTime expiryTimestamp = null; // i will haunt you
//        String token = tokenIDGenerator.generateTokenID(uin, partnerId);
//        IntFunction<String> saltRetreivalFunction = a -> "Test";
//        Map<? extends String, ? extends Object> idHashAttributes = securityManager
//                .getIdHashAndAttributesWithSaltModuloByPlainIdHash(
//                        uin,
//                        saltRetreivalFunction);
//        CredentialIssueRequestDto credentialRequest = createCredReqDto(
//                uin,
//                partnerId,
//                expiryTimestamp,
//                10,
//                token,
//                "UIN",
//                idHashAttributes);
//        CredentialIssueRequestDto credentialIssueRequestDto = new CredentialIssueRequestDto(credentialRequest);
//        ResponseWrapper<CredentialIssueResponse> credentialIssueResponseWrapper = credentialRequestServiceImpl
//                .createCredentialIssuance(credentialIssueRequestDto);
//        CredentialIssueResponse credentialIssueResponse = credentialIssueResponseWrapper.getResponse();
//        String requestId = credentialIssueResponse.getRequestId();
//
//        CredentialServiceRequestDto credentialServiceRequestDto = new CredentialServiceRequestDto();
//        credentialServiceRequestDto.setCredentialType(credentialIssueRequestDto.getCredentialType());
//        credentialServiceRequestDto.setId(credentialIssueRequestDto.getId());
//        credentialServiceRequestDto.setIssuer(credentialIssueRequestDto.getIssuer());
//        credentialServiceRequestDto.setRecepiant(credentialIssueRequestDto.getIssuer());
//        credentialServiceRequestDto.setSharableAttributes(credentialIssueRequestDto.getSharableAttributes());
//        credentialServiceRequestDto.setUser(credentialIssueRequestDto.getUser());
//        credentialServiceRequestDto.setRequestId(requestId);
//        credentialServiceRequestDto.setEncrypt(credentialIssueRequestDto.isEncrypt());
//        credentialServiceRequestDto.setEncryptionKey(credentialIssueRequestDto.getEncryptionKey());
//        credentialServiceRequestDto.setAdditionalData(credentialIssueRequestDto.getAdditionalData());
//        String responseString = restUtil.postApi(ApiName.CRDENTIALSERVICE, null, "", "",
//                MediaType.APPLICATION_JSON, credentialServiceRequestDto, String.class);
//        CredentialServiceResponseDto responseObject = mapper.readValue(responseString,
//                CredentialServiceResponseDto.class);
//
//        CredentialEntity credential = new CredentialEntity();
//        if (responseObject != null &&
//                responseObject.getErrors() != null && !responseObject.getErrors().isEmpty()) {
//            ErrorDTO error = responseObject.getErrors().get(0);
//            credential.setStatusCode(CredentialStatusCode.FAILED.name());
//            credential.setStatusComment(error.getMessage());
//        } else {
//            CredentialServiceResponse credentialServiceResponse = responseObject.getResponse();
//            credential.setCredentialId(credentialServiceResponse.getCredentialId());
//            credential.setDataShareUrl(credentialServiceResponse.getDataShareUrl());
//            credential.setIssuanceDate(credentialServiceResponse.getIssuanceDate());
//            credential.setStatusCode(credentialServiceResponse.getStatus());
//            credential.setSignature(credentialServiceResponse.getSignature());
//            credential.setStatusComment("credentials issued to partner -- on demand");
//        }
//
//        return credential;
//    }
//
//    private CredentialIssueRequestDto createCredReqDto(
//            String id,
//            String partnerId,
//            LocalDateTime expiryTimestamp,
//            Integer transactionLimit,
//            String token,
//            String idType,
//            Map<? extends String, ? extends Object> idHashAttributes) {
//        Map<String, Object> data = new HashMap<>();
//        data.putAll(idHashAttributes);
//        data.put(EXPIRY_TIMESTAMP, Optional.ofNullable(expiryTimestamp).map(DateUtils::formatToISOString).orElse(null));
//        data.put(TRANSACTION_LIMIT, transactionLimit);
//        data.put(TOKEN, token);
//        data.put(ID_TYPE, idType);
//
//        CredentialIssueRequestDto credentialIssueRequestDto = new CredentialIssueRequestDto();
//        credentialIssueRequestDto.setId(id);
//        credentialIssueRequestDto.setCredentialType(credentialType);
//        credentialIssueRequestDto.setIssuer(partnerId);
//        credentialIssueRequestDto.setRecepiant(credentialRecepiant);
//        credentialIssueRequestDto.setUser(IdRepoSecurityManager.getUser());
//        credentialIssueRequestDto.setAdditionalData(data);
//        return credentialIssueRequestDto;
//    }
//}
