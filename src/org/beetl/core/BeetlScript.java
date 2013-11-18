package org.beetl.core;

import java.io.IOException;
import java.io.Reader;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.beetl.core.exception.BeetlException;
import org.beetl.core.parser.BeetlLexer;
import org.beetl.core.parser.BeetlParser;

/**
 * beetl脚本，模板将转为beetl脚本并被执行,核心类之一
 * @author joelli
 *
 */
public class BeetlScript {
	
	GroupTemplate gt;
	ParseTree tree  = null;
	String[] staticText = null;
	int locaVarArraySize = 0;
	Object[] cachedArray = null;
	
	
	public BeetlScript(GroupTemplate gt){
		this.gt = gt ;
	}
	
	/**解析脚本
	 * @param reader
	 * @throws BeetlException
	 */
	public void parse(Reader reader,String[] staticText) throws BeetlException,IOException{
		this.staticText = staticText;
		ANTLRInputStream input = new ANTLRInputStream(reader);		
		BeetlLexer lexer = new BeetlLexer(input);		
		CommonTokenStream tokens = new CommonTokenStream(lexer);
	
		BeetlParser parser = new BeetlParser(tokens);
		tree = parser.prog(); // begin parsing at init rule
		//分析
		
		BeetlScriptParserListener  listener = new BeetlScriptParserListener();
		ParseTreeWalker walker = new ParseTreeWalker();
		walker.walk(listener, tree);
		listener.anzlysze();
		//模板的信息
		this.locaVarArraySize = listener.varIndexSize;
		cachedArray = listener.cachedNodeArray.toArray();
		
		
	}
	
	/**执行脚本，输出到bw
	 * @param ctx
	 * @param bt
	 */
	public void execute(Context ctx,ByteWriter bw) throws BeetlException{
		ctx.staticTextArray = staticText;
		ctx.byteWriter = bw;
		ctx.vars = new Object[locaVarArraySize];	
		ctx.cachedArray = this.cachedArray;
		BeetlScriptParserVistor vistor = new BeetlScriptParserVistor(ctx);
		vistor.visit(tree);
		
	}
}