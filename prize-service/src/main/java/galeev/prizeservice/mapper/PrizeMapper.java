package galeev.prizeservice.mapper;

import galeev.prizeservice.dto.PrizeDataDto;
import galeev.prizeservice.dto.PrizeRequestDto;
import galeev.prizeservice.dto.PrizeResponseDto;
import galeev.prizeservice.entity.Prize;
import org.apache.commons.lang3.ObjectUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(imports = ObjectUtils.Null.class)
public interface PrizeMapper {
    @Mapping(target = "adminDescription", source = "prizeDescription")
    PrizeResponseDto map(Prize prize);

    @Mapping(target = "prizeDescription", source = "adminDescription")
    @Mapping(target = "id", source = "id", defaultExpression = "java(null)")
    Prize map(PrizeRequestDto prizeRequestDto);

    @Mapping(target = "winnerId", source = "userId")
    @Mapping(target = "isRolled", source = "rolled")
    PrizeDataDto mapData(Prize prize);
}
