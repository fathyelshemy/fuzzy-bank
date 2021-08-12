package com.blueharvest.bank.config.exceptions;

import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Getter
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
public final class ResourceNotFoundException extends RuntimeException {

    private int code;
}