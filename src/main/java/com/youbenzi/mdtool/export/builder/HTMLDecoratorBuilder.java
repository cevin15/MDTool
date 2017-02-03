package com.youbenzi.mdtool.export.builder;

import com.youbenzi.mdtool.export.Decorator;
import com.youbenzi.mdtool.export.HTMLDecorator;

public class HTMLDecoratorBuilder implements DecoratorBuilder{

	public Decorator build() {
		return new HTMLDecorator();
	}

}
