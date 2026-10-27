import React from 'react';
import { Button, ButtonProps } from '@mui/material';

type AppButtonKind = 'primary' | 'secondary' | 'text' | 'danger';
type Props = ButtonProps & { kind?: AppButtonKind };

const AppButton: React.FC<Props> = ({ kind = 'secondary', sx, ...props }) => {
  const appearance = {
    primary: { variant: 'contained' as const, color: '#fff', backgroundColor: '#AA0000', borderColor: '#AA0000', '&:hover': { backgroundColor: '#880000' } },
    secondary: { variant: 'outlined' as const, color: '#333', backgroundColor: '#fff', borderColor: '#777', '&:hover': { backgroundColor: '#f5f5f5', borderColor: '#555' } },
    text: { variant: 'text' as const, color: '#7A0000', backgroundColor: 'transparent' },
    danger: { variant: 'outlined' as const, color: '#AA0000', backgroundColor: '#fff', borderColor: '#AA0000', '&:hover': { backgroundColor: '#fff4f4', borderColor: '#880000' } },
  }[kind];
  const { variant, ...appearanceSx } = appearance;

  return (
    <Button
      {...props}
      variant={variant}
      sx={{
        minWidth: kind === 'text' ? 'auto' : 112,
        minHeight: kind === 'text' ? 36 : 40,
        borderRadius: 2,
        fontWeight: kind === 'text' ? 500 : 700,
        textTransform: 'none',
        ...appearanceSx,
        ...(sx as object),
      }}
    />
  );
};

export default AppButton;
