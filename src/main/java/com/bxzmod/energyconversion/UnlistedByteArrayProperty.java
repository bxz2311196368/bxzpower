package com.bxzmod.energyconversion;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;

import net.minecraftforge.common.property.IUnlistedProperty;

public class UnlistedByteArrayProperty implements IUnlistedProperty<byte[]>
{
	protected final String name;

	public UnlistedByteArrayProperty(String name)
	{
		this.name = name;
	}

	@Override
	public String getName()
	{
		return name;
	}

	@Override
	public boolean isValid(byte[] value)
	{
		return true;
	}

	@Override
	public Class<byte[]> getType()
	{
		return byte[].class;
	}

	@Override
	public String valueToString(byte[] value)
	{
		ToStringHelper helper = Objects.toStringHelper("ByteArray");
		if (value != null)
		{
			for (int i1 = 0; i1 < value.length; i1++)
			{
				byte i = value[i1];
				helper.add("Index:" + i1, i);
			}
		}
		return helper.toString();
	}

}
