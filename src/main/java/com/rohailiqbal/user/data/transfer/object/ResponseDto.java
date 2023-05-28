package com.rohailiqbal.user.data.transfer.object;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * The Response class represents a data transfer object for messages.
 * It contains a response message in the form of a JSON.
 * This class is designed to be used to send messages as responses.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResponseDto {
    private String responseMessage;
}