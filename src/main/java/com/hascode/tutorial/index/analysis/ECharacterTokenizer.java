package com.hascode.tutorial.index.analysis;

import java.io.Reader;

import org.apache.lucene.analysis.util.CharTokenizer;
import org.apache.lucene.util.Version;

public class ECharacterTokenizer extends CharTokenizer {
	public ECharacterTokenizer(final Version matchVersion, final Reader input) {
		super(matchVersion, input);
	}

	@Override
	protected boolean isTokenChar(final int character) {
		return 'e' != character;
	}

}
