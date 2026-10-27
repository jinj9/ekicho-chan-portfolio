import { Button } from '@mui/material';
import React, { ReactEventHandler } from 'react';
import { COLOR_THEME } from './Const';

/** 共通ボタンコンポーネント */
interface Props {
  title: string;
  onClickeEvent: () => void;
  size?: "small" | "medium" | "large";
  disabled?: boolean | undefined;
  isDisplay?: boolean | undefined;
}
const ComButton: React.FC<Props> = ({
  size = 'medium',
  disabled = false,
  isDisplay = false,
  ...props
}) => {

  return (
    <>
      <Button
        sx={{color : 'white'}}
        style={{
          backgroundColor: COLOR_THEME.red
        }}
        onClick={props.onClickeEvent}
        size={size}
        disabled={disabled}
      >
        {props.title}
      </Button>
    </>
  );
}

export default ComButton;