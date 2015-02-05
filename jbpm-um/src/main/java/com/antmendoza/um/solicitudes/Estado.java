package com.antmendoza.um.solicitudes;

public enum Estado {
	VALIDADO, NO_VALIDADO, REVISAR;
	
	
	public static boolean isRevisar(String estado){
		return Estado.REVISAR.equals(Estado.valueOf(estado));
	}
	public static boolean isValidado(String estado){
		return Estado.VALIDADO.equals(Estado.valueOf(estado));
	}
	public static boolean isNoValidado(String estado){
		return Estado.NO_VALIDADO.equals(Estado.valueOf(estado));
	}
}
