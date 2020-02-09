package org.hung.pojo;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Data;

@Data
public class AccountTxn {

	private long txRef;
	private LocalDateTime txDatetime;
	private String cardNo;
	private BigDecimal creditLimit;
	private BigDecimal crAmt;
	private BigDecimal drAmt;
	private boolean posted;
	private LocalDateTime postDatetime;
	
	public BigDecimal getNetAmt() {
		BigDecimal netAmt = BigDecimal.ZERO;
		if (crAmt!=null) {
			netAmt = netAmt.add(crAmt);
		}
		if (drAmt!=null) {
			netAmt = netAmt.subtract(drAmt);
		}
		return netAmt;
	}

}
