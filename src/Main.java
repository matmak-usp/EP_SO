import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {
	
	//static Map<Integer, Integer> qtdTrocasPorProcesso = new HashMap<Integer, Integer>();
	static Map<Integer, List<Integer>> processos = new HashMap<Integer, List<Integer>>();

	public static void main(String[] args) {
		Escalonador escalonador;
		PrintStream out;
		int quantum = 0;
		
		try {
			
			File quantumFile = new File(System.getProperty("user.dir") + File.separator + "quantum.txt");
			BufferedReader leitor = new BufferedReader(new FileReader(quantumFile));
			quantum = Integer.parseInt(leitor.readLine());
			escalonador = new Escalonador(quantum);
			
			out = new PrintStream(new FileOutputStream("log"+ String.format("%02d", quantum) +".txt"));
			System.setOut(out);
			
			File listaDePrioridades = new File(System.getProperty("user.dir") + File.separator + "prioridades.txt");
			leitor = new BufferedReader(new FileReader(listaDePrioridades));
			String prioridade;
			int linha = 1;
			
			while((prioridade = leitor.readLine()) != null) { 
				escalonador.novoProcesso(new BCP(linha, Integer.parseInt(prioridade)));
				linha++;
			}
			
			leitor.close();
			
			escalonador.executar();
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		System.out.println("QUANTUM: "+ quantum);
		
		double qtdTotalInstrucoes = 0;
		double qtdQuanta = 0;		
		for (Map.Entry<Integer, List<Integer>> entry : processos.entrySet()) {	        
			qtdTotalInstrucoes += entry.getValue().stream().mapToInt(a -> a).sum();
			qtdQuanta += entry.getValue().stream().mapToInt(a -> a).count();
	    }
		
		System.out.println("Media de instrucoes por quantum: "+ qtdTotalInstrucoes / qtdQuanta);
		System.out.println("Media de trocas de processos: "+ (double) qtdQuanta / processos.size());
	}

}
