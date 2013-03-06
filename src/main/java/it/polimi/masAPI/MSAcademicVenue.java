package it.polimi.masAPI;


public enum MSAcademicVenue {
	TSE		("IEEE Trans. Software Eng.",			"IEEE Transactions on Software Engineering"),
	TOSEM	("ACM Trans. Softw. Eng. Methodol.",	"ACM Transactions on Software Engineering and Methodology"),
	ICSE	("ICSE",								"International Conference on Software Engineering"),
	ASE		("ASE",									"Automated Software Engineering");
	
	private final String dblpName;
	private final String msAcademicName;
	
	private MSAcademicVenue(String dblpName, String msAcademicName) {
		this.dblpName = dblpName;
		this.msAcademicName = msAcademicName;
	}
	
	public static String getMSAcademicName(String dblpName){
		for(MSAcademicVenue msAcademicVenue : MSAcademicVenue.values()){
			if(msAcademicVenue.dblpName.equals(dblpName)){
				return msAcademicVenue.msAcademicName;
			}
		}
		return "";
	}
}
