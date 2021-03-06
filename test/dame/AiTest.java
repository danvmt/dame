package dame;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AiTest {
	Board b;
	Move move = new Move(2,1,3,0);
	Ai ai;
	@BeforeEach
	void setUp() throws Exception {
		 b = new Board();
		 b.placePieces();
		 ai = new Ai(Board.color.BLACK);
	}

	@Test
	void GetAiColorTest() {
		assertEquals("BLACK", ai.getColor().toString());
	}

}
