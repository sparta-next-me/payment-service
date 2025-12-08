package org.nextme.payment_service.common.success;

import org.nextme.infrastructure.success.SuccessReasonDTO;

public interface BaseSuccessCode {

    SuccessReasonDTO getReasonHttpStatus();
}
