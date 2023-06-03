package backend;

import backend.domain.ChessGame;
import backend.models.GameState;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.util.Scanner;

@SpringBootApplication
@Configuration
@EnableAutoConfiguration
@ComponentScan
public class ApplicationStart {
	public static void main(String[] args) {
		//SpringApplication.run(ApplicationStart.class, args);
		ChessGame chessGame = new ChessGame();

		while(chessGame.gameState != GameState.Checkmate){

			Scanner in = new Scanner(System.in);
			System.out.println("Move... turn: " + chessGame.getTurn());
			chessGame.printBoard();
			String input = in.next();
			chessGame.Move(input);

			System.out.println("entered "+input);
		}
	}
}
