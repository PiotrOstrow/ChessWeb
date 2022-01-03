package com.github.piotrostrow.chess.mapper;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MapperConfig {

	@Bean
	public ModelMapper modelMapper() {
		// TODO: more mapping tests
		ModelMapper modelMapper = new ModelMapper();
		modelMapper.addConverter(new GameConverter());
		modelMapper.addConverter(new PuzzleConverter());

		return modelMapper;
	}
}
