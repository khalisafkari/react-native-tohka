import React from 'react'
import { requireNativeComponent } from 'react-native';

const TKTohka = requireNativeComponent('TKTohka', Bann);

export class BannerView extends React.Component{
    render() {
        const {style} = this.props
        return(
            <TKTohka
                style={style}
                {...this.props}
            />
        )
    }
}
