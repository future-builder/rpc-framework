package cdu.mayong.remoting.dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString

public class RpcMessage {
    private byte compress;
    private byte codec;
    private byte messageType;
    private int requestId;
    private Object data;
}
