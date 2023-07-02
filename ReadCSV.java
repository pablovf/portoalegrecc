import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.*;
//import java.util.Vector;
//import java.lang.*;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class ReadCSV {

	private final String URLString = "https://maps.googleapis.com/maps/api/geocode/json?address=";
	private final int ADDRESS_COLUMN_INDEX = 0;

	public static void main(String[] args) {
		ReadCSV obj = new ReadCSV();
		obj.run();
	}

	public void run() {
		String csvFile = "./bairros_canteiros.csv";
		BufferedReader br = null;
		String csvSplitBy = ";"; //Aqui é o delimitador entre as colunas. Por padrão utiliza-se vírgula, porém vírgulas podem
		//estar contidas no endereço, então eu substituí por ;. Não usei delimitadores pois meu CSV contém apenas 1 coluna por linha
		String line = "";
		String aspa = "\"";
                String texto="";
		try {
                    
                    // Gera arquivo XML ou CSV ???
                  FileOutputStream fileOut = new FileOutputStream("./Lat_long_canteiros.txt");
                  OutputStreamWriter xml = new OutputStreamWriter(fileOut);
      
                  GeoMetaData gmd = new GeoMetaData();
            
                  //Abre tag de elemento raiz
                  //texto = "<?xml version=\"1.0\" ?>"+ "\r\n";
                  //texto += "<!DOCTYPE GeoCodeAddress SYSTEM \"funcaoCusto.dtd\">"+ "\r\n";
                  //texto += "<" + gmd.ELEMENTO_RAIZ + ">" + "\r\n";
                  
                  //xml.write(texto);
                  //xml.flush();

			br = new BufferedReader(new FileReader(csvFile));
			//Lê cada linha do csv
			while ((line = br.readLine()) != null) {
				String[] addressArray = line.split(csvSplitBy);

				//Pega a primeira coluna do CSV (no meu caso, o index é 0 pois possuo apenas 1 coluna por linha)
				String address = addressArray[ADDRESS_COLUMN_INDEX];
				//Retira as aspas da linha e usa como parâmetro na requisição HttpGet
				address = address.replaceAll(aspa, "");
                                
                                // Começa a preparar as linhas do XML de saída com o endereço por escrito
                                //texto = " <" + gmd.ELEMENTO_LINHA + ">" + "\r\n";
                                // texto += "   <" + gmd.ELEMENTO_NOME + ">" + address + "</" + gmd.ELEMENTO_NOME + ">" + "\r\n";
                                
                                texto = address + "\t";
                                
				//Substitui os espaços por %20
				address = address.replaceAll(" ", "%20");

				String json = this.sendHttpGetResponse(address);
				
				//A latitude e longitude serão impressas aqui. É só dar o tratamento que deseja
				System.out.println(this.jsonToLocation(json));
				
                                 
                                 //texto += "   <" + gmd.ELEMENTO_LAT + ">" + this.jsonToLatitude(json) + "</" + gmd.ELEMENTO_LAT + ">" + "\r\n";
                                 //texto += "   <" + gmd.ELEMENTO_LONG + ">" + this.jsonToLongitude(json) + "</" + gmd.ELEMENTO_LONG  + ">" + "\r\n";
                                 //texto += " </" + gmd.ELEMENTO_LINHA + ">" + "\r\n";
                                 
                                texto = texto + this.jsonToLatitude(json)+ "\t" + this.jsonToLongitude(json)+"\n";
                                xml.write(texto);
                                 xml.flush();
                                
			}
                        //Fecha tag de elemento raiz
                      //texto = "</" + gmd.ELEMENTO_RAIZ + ">" + "\r\n";;
                      //xml.write(texto);
                      //xml.flush();

                      //Fecha arquivo XML
                      xml.close();
        
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	// HTTP GET request
	private String sendHttpGetResponse(String parameter) throws IOException {
		String fullURL = URLString + parameter;
		
		//Pode deletar esta linha
		System.out.println("sendHttpGetResponse. URL = " + fullURL);
		//Pode deletar esta linha
		System.out.print("Por favor, Aguarde... ");
		
		URL obj = new URL(fullURL);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
		con.setRequestMethod("GET");

		int responseCode = con.getResponseCode();
		if(responseCode == 200) {
			System.out.println("Retornou com sucesso!");
			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();

			return response.toString();
		}
		
		//Pode deletar esta linha
		System.out.println("Retornou erro ");
		
		return null;
	}

	private String jsonToLocation(String json) {
		JSONParser parser = new JSONParser();
		Object obj = null;
		try {
			//Faz o parser de String json para JSONObject
			obj = parser.parse(json);
			JSONObject jsonObject = (JSONObject)obj;

			//Obtém o JSONArray results
			JSONArray resultsArray = (JSONArray)jsonObject.get("results");

			//Obtém o conteúdo do JSONArray results dentro do JSONArray results
			JSONObject results = (JSONObject)resultsArray.get(0);

			//Obtém o JSONObject geometry dentro de results
			JSONObject geometry = (JSONObject)results.get("geometry");

			//Obtém o JSONObject location dentro de geometry
			JSONObject location = (JSONObject)geometry.get("location");

			//Obtém a latitude e longitude dentro de location
			Double latitude = (Double)location.get("lat");
                        
			Double longitude = (Double)location.get("lng");
			
			//Retorna a latitude/longitude formatadas no padrão
			return latitude + "," + longitude;
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}
        
        private String jsonToLatitude(String json) {
		JSONParser parser = new JSONParser();
		Object obj = null;
		try {
			//Faz o parser de String json para JSONObject
			obj = parser.parse(json);
			JSONObject jsonObject = (JSONObject)obj;

			//Obtém o JSONArray results
			JSONArray resultsArray = (JSONArray)jsonObject.get("results");

			//Obtém o conteúdo do JSONArray results dentro do JSONArray results
			JSONObject results = (JSONObject)resultsArray.get(0);

			//Obtém o JSONObject geometry dentro de results
			JSONObject geometry = (JSONObject)results.get("geometry");

			//Obtém o JSONObject location dentro de geometry
			JSONObject location = (JSONObject)geometry.get("location");

			//Obtém a latitude dentro de location
			Double latitude = (Double)location.get("lat");
                        			
			//Retorna a latitude/longitude formatadas no padrão
			return latitude + "";
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}
        
        
        private String jsonToLongitude(String json) {
		JSONParser parser = new JSONParser();
		Object obj = null;
		try {
			//Faz o parser de String json para JSONObject
			obj = parser.parse(json);
			JSONObject jsonObject = (JSONObject)obj;

			//Obtém o JSONArray results
			JSONArray resultsArray = (JSONArray)jsonObject.get("results");

			//Obtém o conteúdo do JSONArray results dentro do JSONArray results
			JSONObject results = (JSONObject)resultsArray.get(0);

			//Obtém o JSONObject geometry dentro de results
			JSONObject geometry = (JSONObject)results.get("geometry");

			//Obtém o JSONObject location dentro de geometry
			JSONObject location = (JSONObject)geometry.get("location");

			//Obtém a latitude dentro de location
			Double longitude = (Double)location.get("lng");
                        			
			//Retorna a latitude/longitude formatadas no padrão
			return longitude + "";
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}
}